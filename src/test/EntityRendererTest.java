package test;

import model.block.BlockModel;
import model.entity.EntityCoordinate;
import model.entity.EntityModel;
import view.entity.EntityRenderer;

public class EntityRendererTest {
    public static void main(String[] args) {
        TestEntity rabbit = new TestEntity(new EntityCoordinate(4.5, 3.0));
        EntityRenderer renderer = new EntityRenderer(rabbit, "assets/rabbit.png", 'R', 32, 32);

        assertEquals("entity type", "TestEntity", renderer.getEntityType());
        assertEquals("sprite path", "assets/rabbit.png", renderer.getSpritePath());
        assertEquals("console symbol", 'R', renderer.getConsoleSymbol());
        assertEquals("initial screen x", 4, renderer.getScreenX());
        assertEquals("initial screen y", 3, renderer.getScreenY());
        assertEquals("width", 32, renderer.getWidth());
        assertEquals("height", 32, renderer.getHeight());
        assertEquals("visible", true, renderer.isVisible());
        assertEquals("current state", 0, renderer.getCurrentState());

        rabbit.setPosition(new EntityCoordinate(6.0, 5.5));
        rabbit.setCurrentState(1);
        renderer.updateScreenPosition(32, 64, 32);

        assertEquals("moved screen x", 128, renderer.getScreenX());
        assertEquals("moved screen y", 144, renderer.getScreenY());
        assertEquals("updated current state", 1, renderer.getCurrentState());

        renderer.setVisible(false);
        renderer.setSpritePath("assets/rabbit_adult.png");
        renderer.setConsoleSymbol('A');
        renderer.setWidth(48);
        renderer.setHeight(48);

        assertEquals("updated visible", false, renderer.isVisible());
        assertEquals("updated sprite path", "assets/rabbit_adult.png", renderer.getSpritePath());
        assertEquals("updated console symbol", 'A', renderer.getConsoleSymbol());
        assertEquals("updated width", 48, renderer.getWidth());
        assertEquals("updated height", 48, renderer.getHeight());

        System.out.println("EntityRendererTest passed.");
    }

    private static void assertEquals(String name, Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError(name + " expected " + expected + " but got " + actual);
        }
    }

    private static class TestEntity extends EntityModel {
        public TestEntity(EntityCoordinate position) {
            super(position, 100, 0, 5, 10, 15, 0);
        }

        @Override
        public void Interact(BlockModel block) {
        }

        @Override
        public void Interact(EntityModel entity) {
        }
    }
}
