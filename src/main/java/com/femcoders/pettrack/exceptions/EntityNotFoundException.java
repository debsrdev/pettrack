package com.femcoders.pettrack.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, Long id) {
        super(entityName + " not found with id " + id);
    }
}
