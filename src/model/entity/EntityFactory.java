package model.entity;

import model.animals.Cow;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class EntityFactory {
    private static Map<String, Function<EntityCoordinate, EntityModel>> entityCreationMap;

    public static void register(String entityType, Function<EntityCoordinate, EntityModel> constructor){
        if(entityCreationMap == null) entityCreationMap = new HashMap<>();
        entityCreationMap.put(entityType, constructor);
        IO.println("Registered factory for " + entityType);
    }

    private static EntityModel create(String entityType, EntityCoordinate coordinate){
        Function<EntityCoordinate, EntityModel> entityCreation = entityCreationMap.get(entityType);
        if(entityCreation == null) return null;
        if(coordinate == null) return null;
        return entityCreation.apply(new EntityCoordinate(coordinate));
    }

    public static EntityModel createFrom(EntityModel entity){
        return create(entity.getEntityType(), entity.getPosition());
    }
}
