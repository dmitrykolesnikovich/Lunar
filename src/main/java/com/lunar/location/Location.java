package com.lunar.location;

import com.lunar.utilities.MathHelper;
import com.lunar.world.World;

public class Location {

    private double x, y;
    private boolean onGround;

    private World world;

    /**
     * Initialize the location.
     */
    public Location(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Initialize the location.
     */
    public Location(double x, double y, boolean onGround) {
        this.x = x;
        this.y = y;

        this.onGround = onGround;

    }

    /**
     * Initialize the location.
     */
    public Location(double x, double y, World world) {
        this.x = x;
        this.y = y;

        this.world = world;
    }

    /**
     * Initialize the location.
     */
    public Location(double x, double y, World world, boolean onGround) {
        this.x = x;
        this.y = y;
        this.onGround = onGround;
        this.world = world;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y The y to set
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Return if we are on ground or not.
     */
    public boolean isOnGround() {
        return onGround;
    }

    /**
     * Set if we are on ground or not.
     */
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    /**
     * Get the world.
     */
    public World getWorld() {
        return world;
    }

    /**
     * Set the world.
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Clone this location.
     */
    public Location clone() {
        return new Location(x, y);
    }

    /**
     * Get the distance between two podoubles.
     */
    public double distance(Location location) {
        double dX = location.getX() - x;
        double dY = location.getY() - y;
        return Math.sqrt(dX * dX + dY * dY);
    }

    /**
     * Get the distance between two podoubles.
     */
    public double distance(double x, double y) {
        double dX = x - this.x;
        double dY = y - this.y;
        return Math.sqrt(dX * dX + dY * dY);
    }

    /**
     * Add to this location.
     */
    public Location add(Location other) {
        return new Location(x + other.getX(), y + other.getY());
    }

    /**
     * Subtract this location by another.
     */
    public Location subtract(Location other) {
        return new Location(x - other.getX(), y - other.getY());
    }

    /**
     * Multiply this location by another.
     */
    public Location multiply(Location other) {
        return new Location(x * other.getX(), y * other.getY());
    }

    /**
     * Divide this location by another.
     */
    public Location divide(Location other) {
        return new Location(x / other.getX(), y / other.getY());
    }

    /**
     * Add value to x and y of this location.
     */
    public Location scalarAdd(double value) {
        return new Location(x + value, y + value);
    }

    /**
     * Subtract value from x and y of this location.
     */
    public Location scalarSub(double value) {
        return new Location(x - value, y - value);
    }

    /**
     * Subtract x and y from value.
     */
    public Location scalarPreSub(double value) {
        return new Location(value - x, value - y);
    }

    /**
     * Multiply x and y by value
     */
    public Location scalarMultiply(double value) {
        return new Location(x * value, y * value);
    }

    /**
     * Divide x and y by value
     */
    public Location scalarDivide(double value) {
        return new Location(x / value, y / value);
    }

    /**
     * Divide value by x and y
     */
    public Location scalarPreDivide(double value) {
        return new Location(value / x, value / y);
    }

    /**
     * Set this location as the lerp from one location to another location by step t. See Utilites#lerp.
     * <p>
     * NOTE: This modifies this location
     */
    public Location lerpSelf(Location from, Location to, double t) {
        Location lerped = MathHelper.lerp(from, to, t);
        this.setX(lerped.getX());
        this.setY(lerped.getY());

        return this;
    }

    /**
     * Lerp this location to another location by step t. See Utilites#lerp.
     * <p>
     * NOTE: This modifies this location
     */
    public Location lerpSelf(Location to, double t) {
        Location lerped = MathHelper.lerp(this, to, t);
        this.setX(lerped.getX());
        this.setY(lerped.getY());

        return this;
    }

    /**
     * Lerp from this location to another by t, returns a new Location object, does not
     * modify this location.
     */
    public Location lerp(Location to, double t) {
        return MathHelper.lerp(this, to, t);
    }


    /**
     * Lerp from one location to another with progress t. See Utilites#lerp
     */
    public static Location lerp(Location from, Location to, double t) {
        return MathHelper.lerp(from, to, t);
    }

    /**
     * Two Locations are considered equal if their x's and y's are the same.
     * <p>
     * The world instance is not checked as Locations are often instantiated without
     * a world object available in the context.
     *
     * @param obj the other object
     * @return true/false whether the objects are referentially or logically equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (obj instanceof Location) {
            Location other = (Location) obj;

            return this.x == other.x && this.y == other.y;
        }

        return super.equals(obj);
    }
    // TODO: hashcode function
}
