package aigen.feature;

//Vector class (placeholder - implement based on your Geometry module)
class Vector {
 private double x, y, z;

 public Vector(double x, double y, double z) {
     this.x = x;
     this.y = y;
     this.z = z;
 }

 public Vector add(Vector other) {
     return new Vector(this.x + other.x, this.y + other.y, this.z + other.z);
 }

 public Vector divide(int divisor) {
     return new Vector(this.x / divisor, this.y / divisor, this.z / divisor);
 }
}

