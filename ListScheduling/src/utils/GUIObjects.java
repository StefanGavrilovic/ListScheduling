/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Utility class for creating new JavaFX GUI objects.
 *
 * @author zer0
 */
public class GUIObjects {

    /**
     * Utility class should not have constructor.
     */
    private GUIObjects() {
    }

    /**
     * This method creates default circle.
     *
     * @param radius {@link Double} Radius for the circle.
     *
     * @return {@link Circle} Returns created circle.
     */
    public static final Circle createCircle(final double radius) {
        return createCircle(radius, Color.WHITE);
    }

    /**
     * This method creates circle filled with the given color.
     *
     * @param radius {@link Double} Radius for the circle.
     * @param color {@link Color} Color of the circle to be created.
     *
     * @return {@link Circle} Returns created circle.
     */
    public static final Circle createCircle(final double radius, final Paint color) {
        final Circle circle = new Circle(radius);
        circle.setFill(color);
        circle.setStroke(Color.BLACK);
        return circle;
    }

    /**
     * This method creates default circle.
     *
     * @param radius {@link Double} Radius for the circle.
     *
     * @return {@link Circle} Returns created circle as group.
     */
    public static final Group createCircleGroup(final double radius) {
        return createCircleGroup(radius, Color.WHITE);
    }

    /**
     * This method creates circle filled with the given color.
     *
     * @param radius {@link Double} Radius for the circle.
     * @param color {@link Color} Color of the circle to be created.
     *
     * @return {@link Group} Returns created circle as group.
     */
    public static final Group createCircleGroup(final double radius, final Paint color) {
        final Group circle = new Group();
        circle.getChildren().add(createCircle(radius, color));
        return circle;
    }


    /**
     * This method creates default rectangle with the given width and height.
     *
     * @param width {@link Double} Width of the rectangle.
     * @param height {@link Double} Height of the rectangle.
     *
     * @return {@link Rectangle} Returns created rectangle.
     */
    public static final Rectangle createRectangle(final double width, final double height) {
        final Rectangle box = new Rectangle(width, height);
        box.setFill(Color.WHITE);
        box.setArcHeight(40);
        box.setArcWidth(40);
        return box;
    }

    /**
     * This method creates rectangle with the given width and height and filled
     * with the given color.
     *
     * @param width {@link Double} Width of the rectangle.
     * @param height {@link Double} Height of the rectangle.
     * @param color {@link Color} Color of the rectangle to be created.
     *
     * @return {@link Rectangle} Returns created rectangle.
     */
    public static final Rectangle createRectangle(final double width, final double height, final Paint color) {
        final Rectangle box = new Rectangle(width, height);
        box.setFill(color);
        box.setArcHeight(40);
        box.setArcWidth(40);
        return box;
    }

}
