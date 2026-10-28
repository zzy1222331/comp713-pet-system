package com.comp713;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/pets")
@Produces(MediaType.APPLICATION_JSON)
public class PetResource {

    @Inject
    PetRepository repository;

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
            // Trigger WebSocket broadcast to notify all connected web clients
            PetUpdateSocket.broadcast("New pet added: " + name + " (" + species + ")");
            
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