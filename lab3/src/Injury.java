public enum Injury {
    HIP,
    KNEE,
    ELBOW;

    static public Injury fromString(String string){
        switch (string.toUpperCase()) {
            case "HIP":
                return Injury.HIP;
            case "KNEE":
                return Injury.KNEE;
            case "ELBOW":
                return Injury.ELBOW;
            default:
                throw new IllegalArgumentException();
        }
    }
}