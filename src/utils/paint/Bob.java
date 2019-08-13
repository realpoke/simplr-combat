package utils.paint;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.model.Vector3D;
import org.osbot.rs07.api.util.GraphicUtilities;
import org.osbot.rs07.script.API;

public class Bob extends API {

    @Override
    public void initializeModule() {
    }

    /**
     * Draw line between two in-game points
     *
     * @param g
     *            - Graphic object
     * @param entity1
     *            - First entity point
     * @param entity2
     *            - Second entity point
     */
    public void drawLink(Graphics2D g, Entity entity1, Entity entity2) {
        Area a1;
        Area a2;
        Rectangle2D r1;
        Rectangle2D r2;
        Point p1;
        Point p2;
        if (entity1 != null && entity2 != null) {
            a1 = GraphicUtilities.getModelArea(bot, entity1.getGridX(), entity1.getGridY(), entity1.getZ(), entity1.getModel());
            a2 = GraphicUtilities.getModelArea(bot, entity2.getGridX(), entity2.getGridY(), entity2.getZ(), entity2.getModel());
            r1 = a1.getBounds2D();
            r2 = a2.getBounds();
            p1 = new Point((int) r1.getCenterX(), (int) r1.getCenterY());
            p2 = new Point((int) r2.getCenterX(), (int) r2.getCenterY());
            if ((p1.x > 0 || p1.y > 0) && (p2.x > 0 || p2.y > 0)) {
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }

    /**
     * Draw minimap area
     *
     * @param g
     *            - Graphic object
     * @param area
     *            - Area object
     */
    public void drawMinimapArea(Graphics2D g, org.osbot.rs07.api.map.Area area) {

        Polygon areaPolygon;
        Polygon minimapPolygon;
        int minimapX;
        int minimapY;
        short[] minimapCoordinates;

        if (area != null) {

            areaPolygon = area.getPolygon();

            if (areaPolygon != null) {

                minimapPolygon = new Polygon();

                for (int i = 0; i < areaPolygon.npoints; i++) {

                    minimapX = areaPolygon.xpoints[i];
                    minimapY = areaPolygon.ypoints[i];

                    minimapCoordinates = GraphicUtilities.getMinimapScreenCoordinate(bot, minimapX, minimapY);

                    if (minimapCoordinates != null) {

                        minimapX = (int) minimapCoordinates[0];
                        minimapY = (int) minimapCoordinates[1];

                        minimapPolygon.addPoint(minimapX, minimapY);
                    }

                }

                drawShape(g, minimapPolygon);
            }
        }
    }

    /**
     * Draw a dynamically generated, position-related string and set its location to
     * an in-game position
     *
     * @param g
     *            - Graphic object
     * @param toString
     *            - string function
     * @param position
     *            - Position object
     */
    public <T extends Position> void drawString(Graphics2D g, Function<T, String> toString, T position) {
        if (toString != null && position != null) {
            drawString(g, toString.apply(position), position);
        }
    }

    /**
     * Draw string for a tile
     *
     * @param g
     *            - Graphic object
     * @param aString
     *            - String object
     * @param position
     *            - Position object
     * @see {@link Bob#drawString(Graphics2D, String, Rectangle)}
     */
    public void drawString(Graphics2D g, String aString, Position position) {

        Rectangle2D rectangle;
        Polygon polygon;

        if (position != null && position.isVisible(bot)) {

            polygon = position.getPolygon(bot);

            if (polygon != null) {

                rectangle = polygon.getBounds2D();

                drawString(g, aString, rectangle.getBounds());
            }
        }
    }

    /**
     * Draw string for an entity
     *
     * @param g
     *            - Graphic object
     * @param aString
     *            - String object
     * @param entity
     *            - Entity object
     */
    public void drawString(Graphics2D g, String aString, Entity entity) {

        Rectangle rectangle;

        if (entity != null && entity.isVisible()) {

            rectangle = GraphicUtilities.getModelBoundingBox(bot, entity.getGridX(), entity.getGridY(), entity.getZ(),
                    entity.getModel());

            if (rectangle != null) {

                drawString(g, aString, rectangle);
            }
        }
    }

    /**
     * Draw entity
     *
     * @param g
     *            - Graphic object
     * @param entity
     *            - Entity object
     * @param aString
     *            - String object
     * @param labelTile
     *            - <tt>Label relative to tile</tt>
     * @param click
     *            - <tt>Draw click box</tt>
     * @param cube
     *            - <tt>Draw model cube</tt>
     * @param minimap
     *            - <tt>Draw minimap point</tt>
     * @param tile
     *            - <tt>Draw tile</tt>
     * @param box
     *            - <tt>Draw 3D wire-frame box spanning from bottom to top tile</tt>
     * @param wireframe
     *            - <tt>Draw wire-frame</tt>
     * @see {@link Bob#drawClickBounds(Graphics2D, Entity)}
     * @see {@link Bob#drawCube(Graphics2D, Entity)}
     * @see {@link Bob#drawMinimapPoint(Graphics2D, Vector3D)}
     * @see {@link Bob#drawTile(Graphics2D, Entity)}
     * @see {@link Bob#drawBox(Graphics2D, Entity)}
     * @see {@link Bob#drawWireframe(Graphics2D, Entity)}
     * @see {@link Bob#drawString(Graphics2D, String, Position)}
     * @see {@link Bob#drawString(Graphics2D, String, Entity)}
     */
    public void drawEntity(Graphics2D g, Entity entity, String aString, boolean labelTile, boolean click, boolean cube,
                           boolean minimap, boolean tile, boolean box, boolean wireframe) {

        if (entity != null) {

            if (minimap) {
                drawMinimapPoint(g, entity);
            }

            if (entity.isVisible()) {

                if (click) {
                    drawClickBounds(g, entity);
                }

                if (cube) {
                    drawCube(g, entity);
                }

                if (tile) {
                    drawTile(g, entity);
                }

                if (box) {
                    drawBox(g, entity);
                }

                if (wireframe) {
                    drawWireframe(g, entity);
                }

                if (labelTile) {

                    drawString(g, aString, new Position(entity.getX(), entity.getY(), entity.getZ() + 1));

                } else {

                    drawString(g, aString, entity);
                }
            }
        }
    }

    /**
     * Draw entity
     *
     * @param g
     *            - Graphic object
     * @param entity
     *            - Entity object
     * @param Function<Entity,
	 *            String> - get description
     * @param labelTile
     *            - <tt>Label relative to tile</tt>
     * @param click
     *            - <tt>Draw click box</tt>
     * @param cube
     *            - <tt>Draw model cube</tt>
     * @param minimap
     *            - <tt>Draw minimap point</tt>
     * @param tile
     *            - <tt>Draw tile</tt>
     * @param tileCube
     *            - <tt>Draw tile cube</tt>
     * @param wireframe
     *            - <tt>Draw wireframe</tt>
     * @see {@link Bob#drawEntity(Graphics2D, Entity, String, boolean, boolean, boolean, boolean, boolean, boolean, boolean)}
     */
    public <T extends Entity>  void drawEntity(Graphics2D g, T entity, Function<T, String> getDescription, boolean labelTile, boolean click, boolean cube,
                                               boolean minimap, boolean tile, boolean tileCube, boolean wireframe) {
        String aString = null;
        if (getDescription != null && entity != null) {
            aString = getDescription.apply(entity);
        }
        drawEntity(g, entity, aString, labelTile, click, cube, minimap, tile, tileCube, wireframe);
    }

    /**
     * Draw entities
     *
     * @param g
     *            - Graphic object
     * @param entity
     *            - Entity object
     * @param Function<Entity,
	 *            String> - get description
     * @param labelTile
     *            - <tt>Label relative to tile</tt>
     * @param click
     *            - <tt>Draw click box</tt>
     * @param cube
     *            - <tt>Draw model cube</tt>
     * @param minimap
     *            - <tt>Draw minimap point</tt>
     * @param tile
     *            - <tt>Draw tile</tt>
     * @param tileCube
     *            - <tt>Draw tile cube</tt>
     * @param wireframe
     *            - <tt>Draw wireframe</tt>
     * @see {@link Bob#drawEntity(Graphics2D, Entity , String , boolean , boolean , boolean , boolean , boolean , boolean , boolean )}
     */
    public <T extends Entity> void drawEntities(Graphics2D g, Collection<T> entities, Function<T, String> getDescription,
                                                boolean labelTile, boolean click, boolean cube, boolean minimap, boolean tile, boolean tileCube,
                                                boolean wireframe) {

        if (entities != null && !entities.isEmpty()) {
            entities.forEach(entity -> {

                if (entity != null) {

                    String aString = null;

                    if (getDescription != null) {
                        aString = getDescription.apply(entity);
                    }

                    drawEntity(g, entity, aString, labelTile, click, cube, minimap, tile, tileCube, wireframe);
                }
            });
        }
    }

    /**
     * Draw box
     *
     * @param g
     *            - Graphic object
     * @param entity
     *            - Entity object
     * @see {@link Bob#drawBox(Graphics2D, Position)}
     */
    private void drawBox(Graphics2D g, Entity entity) {
        Position bottomTile;
        Position topTile;
        if (entity != null) {
            bottomTile = entity.getPosition();
            topTile = new Position(bottomTile.getX(), bottomTile.getY(), bottomTile.getZ() + 1);
            drawBox(g, bottomTile, topTile);
        }
    }

    /**
     * Draw box
     *
     * @param g
     *            - Graphic object
     * @param position
     *            - Position object A
     * @param position
     *            - Position object B
     * @see {@link Bob#drawShape(Graphics2D, Shape)}
     */
    private void drawBox(Graphics2D g, Position bottomTile, Position topTile) {

        Polygon bottom;
        Polygon top;

        Polygon side1;
        Polygon side2;
        Polygon side3;
        Polygon side4;

        if (bottomTile != null) {

            bottom = bottomTile.getPolygon(bot);
            top = topTile.getPolygon(bot);

            side1 = new Polygon(
                    new int[] { top.xpoints[0], top.xpoints[1], bottom.xpoints[1], bottom.xpoints[0] },
                    new int[] { top.ypoints[0], top.ypoints[1], bottom.ypoints[1], bottom.ypoints[0] },
                    4);

            side2 = new Polygon(
                    new int[] { top.xpoints[1], top.xpoints[2], bottom.xpoints[2], bottom.xpoints[1] },
                    new int[] { top.ypoints[1], top.ypoints[2], bottom.ypoints[2], bottom.ypoints[1] },
                    4);

            side3 = new Polygon(
                    new int[] { top.xpoints[2], top.xpoints[3], bottom.xpoints[3], bottom.xpoints[2] },
                    new int[] { top.ypoints[2], top.ypoints[3], bottom.ypoints[3], bottom.ypoints[2] },
                    4);

            side4 = new Polygon(
                    new int[] { top.xpoints[3], top.xpoints[0], bottom.xpoints[0], bottom.xpoints[3] },
                    new int[] { top.ypoints[3], top.ypoints[0], bottom.ypoints[0], bottom.ypoints[3] },
                    4);

            drawShape(g, bottom);

            drawShape(g, side1);
            drawShape(g, side2);
            drawShape(g, side3);
            drawShape(g, side4);

            drawShape(g, top);

        }
    }

    /**
     * Draw wireframe
     *
     * @param g
     *            - Graphic object
     * @param entity
     *            - Entity object
     * @see {@link Bob#drawShape(Graphics2D, Shape)}
     */
    private void drawWireframe(Graphics2D g, Entity entity) {

        List<Polygon> polygons = GraphicUtilities.getModelMeshTriangles(bot, entity.getGridX(), entity.getGridY(),
                entity.getZ(), entity.getModel());

        if (polygons != null && !polygons.isEmpty()) {

            for (Polygon polygon : polygons) {

                drawShape(g, polygon);
            }
        }
    }

    /**
     * Draw click bounds
     *
     * @param g
     *            - Graphic object
     * @param entity
     *            - Entity object
     */
    private void drawClickBounds(Graphics2D g, Entity entity) {

        Rectangle rectangle = GraphicUtilities.getModelBoundingBox(bot, entity.getGridX(), entity.getGridY(),
                entity.getZ(), entity.getModel());

        if (rectangle != null) {

            drawShape(g, rectangle);
        }
    }

    /**
     * Draw box around entity
     *
     * @param g
     *            - Graphic object
     * @param entity
     *            - Entity object
     */
    private void drawCube(Graphics2D g, Entity entity) {

        Area area = GraphicUtilities.getCubicArea(bot, entity.getGridX(), entity.getGridY(), entity.getZ(),
                entity.getSizeX(), entity.getSizeY(), entity.getHeight());

        if (area != null) {

            drawShape(g, area);
        }
    }

    /**
     * Draw minimap point
     *
     * @param g
     *            - Graphic object
     * @param v
     *            - 3D Vector
     */
    public void drawMinimapPoint(Graphics2D g, Vector3D v) {

        short[] minimapPosition = null;
        int x = 0;
        int y = 0;

        if (v != null) {

            minimapPosition = GraphicUtilities.getMinimapScreenCoordinate(bot, v.getX(), v.getY());

            if (minimapPosition != null) {

                x = (int) minimapPosition[0];
                y = (int) minimapPosition[1];

                drawPoint(g, x, y, 3);
            }
        }
    }

    /**
     * Draw links between vectors
     *
     * @param g
     *            - Graphic object
     * @param a
     *            - 3D Vector A
     * @param b
     *            - 3D Vector B
     */
    public void drawMinimapLink(Graphics2D g, Vector3D a, Vector3D b) {

        short[] minimapPositionA;
        short[] minimapPositionB;
        int x1;
        int y1;
        int x2;
        int y2;

        if (a != null && b != null) {

            minimapPositionA = GraphicUtilities.getMinimapScreenCoordinate(bot, a.getX(), a.getY());
            minimapPositionB = GraphicUtilities.getMinimapScreenCoordinate(bot, b.getX(), b.getY());

            if (minimapPositionA != null && minimapPositionB != null) {

                x1 = minimapPositionA[0];
                y1 = minimapPositionA[1];
                x2 = minimapPositionB[0];
                y2 = minimapPositionB[1];

                if (x1 != x2 && y1 != y2
                        && (x1 > 0 || y1 > 0)
                        && (x2 > 0 || y2 > 0)) {

                    g.drawLine(x1, y1, x2, y2);
                }
            }
        }
    }

    /**
     * Draw tile
     *
     * @param g
     *            - Graphic object
     * @param entity
     *            - entity object
     * @see {@link Bob#drawTile(Graphics2D, Position)}
     */
    public void drawTile(Graphics2D g, Entity entity) {

        if (entity != null) {

            drawTile(g, entity.getPosition());
        }
    }

    /**
     * Draw tile
     *
     * @param g
     *            - Graphic object
     * @param position
     *            - Position object
     */
    public void drawTile(Graphics2D g, Position position) {

        Polygon polygon = null;

        if (position != null && position.isVisible(bot)) {

            polygon = position.getPolygon(bot);

            drawShape(g, polygon);
        }
    }

    /**
     * Draw point
     *
     * @param g
     *            - Graphic object
     * @param point
     *            - Point object
     * @param size
     *            - Size of the oval
     */
    public static void drawPoint(Graphics2D g, Point point, int size) {

        if (point != null) {

            drawPoint(g, point.x, point.y, size);
        }
    }

    /**
     * Draw point
     *
     * @param g
     *            - Graphic object
     * @param x
     *            - X coordinate
     * @param y
     *            - Y coordinate
     * @param size
     *            - Size of the oval
     */
    public static void drawPoint(Graphics2D g, int x, int y, int size) {

        final Color bg = g.getBackground();
        final Color fg = g.getColor();

        g.setColor(bg);
        g.fillOval(x - size, y - size, size * 2, size * 2);
        g.setColor(fg);
        g.drawOval(x - size, y - size, size * 2, size * 2);

    }

    /**
     * Draw and fill shape
     *
     * @param g
     *            - Graphic object
     * @param shape
     *            - Shape object
     */
    public static void drawShape(Graphics2D g, Shape shape) {

        final Color bg = g.getBackground();
        final Color fg = g.getColor();

        if (shape != null) {

            if (bg != null) {
                g.setColor(bg);
                g.fill(shape);
                g.setColor(fg);
            }
            g.draw(shape);

        }
    }

    /**
     * Draw string
     *
     * @param g
     *            - Graphic object
     * @param aString
     *            - String object
     * @param x
     *            - X coordinate
     * @param y
     *            - Y coordinate
     */
    public static void drawString(Graphics2D g, String aString, int x, int y) {

        final Color bg = g.getBackground();
        final Color fg = g.getColor();

        if (aString != null && !aString.isEmpty()) {

            if (bg != null) {

                g.setColor(bg);
                g.drawString(aString, x + 1, y + 1);
                g.drawString(aString, x + 1, y - 1);
                g.drawString(aString, x - 1, y + 1);
                g.drawString(aString, x - 1, y - 1);
                g.setColor(fg);
            }

            g.drawString(aString, x, y);
        }
    }

    /**
     * Draw string
     *
     * @param g
     *            - Graphic object
     * @param aString
     *            - String object
     * @param point
     *            - Point object
     * @see {@link Bob#drawString(Graphics2D, String, int, int)}
     */
    public static void drawString(Graphics2D g, String aString, Point point) {

        if (point != null) {

            drawString(g, aString, point.x, point.y);
        }
    }

    /**
     * Draw centred string above rectangle
     *
     * @param g
     *            - Graphic object
     * @param aString
     *            - String object
     * @param rectangle
     *            - Rectangle object
     */
    public static void drawString(Graphics2D g, String aString, Rectangle rectangle) {

        final FontMetrics fontMetrics = g.getFontMetrics();

        double x = 0D;
        double y = 0D;
        int stringWidth = 0;

        if (aString != null && !aString.isEmpty() && rectangle != null) {

            stringWidth = fontMetrics.stringWidth(aString);

            x += rectangle.getCenterX();
            x -= (stringWidth / 2);

            y = rectangle.getY();

            drawString(g, aString, (int) x, (int) y);
        }
    }
}