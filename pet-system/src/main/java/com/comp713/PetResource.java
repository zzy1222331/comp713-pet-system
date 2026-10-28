package com.comp713;

import java.util.List;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/pets")
@Produces(MediaType.APPLICATION_JSON)
public class PetResource {

    @Inject
    PetRepository repository;

  
    @Resource(lookup = "jms/PetConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/PetQueue")
    private Queue petQueue;

    @GET
    @Path("/search")
    public Response searchPets(@QueryParam("species") String species) {
        if (species == null || species.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\": \"Species parameter is required\"}").build();
        }
        List<String> pets = repository.findPetsBySpecies(species);
        if (pets.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"message\": \"No pets found\"}").build();
        }
        return Response.ok(pets).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addPet(@FormParam("name") String name, @FormParam("species") String species) {
        if (name == null || name.trim().isEmpty() || species == null || species.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\": \"Name and species are required\"}").build();
        }
        
        boolean success = repository.addPet(name, species);
        
        if (success) {
            
            PetUpdateSocket.broadcast("New pet added: " + name + " (" + species + ")");
            
          
            try (JMSContext context = connectionFactory.createContext("admin", "admin")) {
                String eventMessage = "EVENT: Pet " + name + " (" + species + ") is added to the system!";
                context.createProducer().send(petQueue, eventMessage);
                System.out.println("✅ [Payara] JMS Message Sent to Artemis: " + eventMessage);
            } catch (Exception e) {
                System.out.println("❌ [Payara] JMS Send Failed: " + e.getMessage());
            }

            return Response.status(Response.Status.CREATED)
                           .entity("{\"message\": \"Pet added successfully\"}").build();
        }
        return Response.serverError().entity("{\"error\": \"Database error\"}").build();
    }

    @POST
    @Path("/adopt")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response adoptPet(@FormParam("petId") long petId, @FormParam("userId") long userId) {
        if (repository.applyForAdoption(petId, userId)) {
            return Response.status(Response.Status.CREATED).entity("{\"message\": \"Adoption applied\"}").build();
        }
        return Response.serverError().entity("{\"error\": \"Failed to apply\"}").build();
    }

    @PUT
    @Path("/adopt/approve")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response approveAdoption(@FormParam("adoptionId") long adoptionId, @FormParam("status") String status) {
        if (repository.updateAdoptionStatus(adoptionId, status)) {
            return Response.ok("{\"message\": \"Status updated to " + status + "\"}").build();
        }
        return Response.serverError().entity("{\"error\": \"Failed to update\"}").build();
    }
}