package api;

/** represent the location of an object in 3D space.
 *  can also calculate distance between locations.
 */
public class GeoLocation implements geo_location {

    private double _x, _y, _z;    //position on the axis

    /** constructor to set the position of the object. */
    public GeoLocation(double x, double y, double z){
        _x = x;
        _y = y;
        _z = z;
    }

    /** @return the X value of the position. */
    @Override
    public double x() {
        return _x;
    }

    /** @return the Y value of the position. */
    @Override
    public double y() {
        return _y;
    }

    /** @return the Z value of the position. */
    @Override
    public double z() {
        return _z;
    }

    /** calculate the distance of this position to the next.
     * @param g the location of the wanted object.
     * @return distance of the position in 3D space.
     */
    @Override
    public double distance(geo_location g) { //basic formula of ((x1-x2)^2 + (y1-y2)^2 + (z1+z2)^2)^0.5
        double dist = Math.sqrt(Math.pow((g.x() - _x),2) + Math.pow((g.y() - _y), 2) + Math.pow((g.z() - _z), 2));
        return dist;
    }

    /** @return a string of the position of the object. */
    public String toString(){
        String info = _x + "," + _y + "," + _z;
        return info;
    }
}
