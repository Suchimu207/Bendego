package save;

import java.util.Objects;

public class Version implements Comparable<Version> {
    private final int major;
    private final int minor;
    private final int patch;

    public Version(String version){
        if (version == null || version.isEmpty()){
            throw new IllegalArgumentException(">>Versão não pode ser nula ou vazia.");
        }

        String[] parts = version.split("\\.");
        if (parts.length != 3){
            throw new IllegalArgumentException(">>Formato inválido (esperado X.Y.Z): "+version);
        }
		
        try{
            this.major = Integer.parseInt(parts[0]);
            this.minor = Integer.parseInt(parts[1]);
            this.patch = Integer.parseInt(parts[2]);
        }catch (NumberFormatException e){
            throw new IllegalArgumentException(">>Versão contém valores não numéricos: "+version);
        }
		
        if (major < 0 || minor < 0 || patch < 0){
            throw new IllegalArgumentException(">>Versão não pode conter valores negativos: "+version);
        }
    }
	
    @Override
    public int compareTo(Version outra){
        if (this.major != outra.major) return Integer.compare(this.major, outra.major);
        if (this.minor != outra.minor) return Integer.compare(this.minor, outra.minor);
        return Integer.compare(this.patch, outra.patch);
    }
	
    public boolean isMaiorQue(Version outra){ return this.compareTo(outra) > 0; }
    public boolean isMenorQue(Version outra){ return this.compareTo(outra) < 0; }
    public boolean isIgualA(Version outra){ return this.compareTo(outra) == 0; }

    public int getMajor(){ return major; }
    public int getMinor(){ return minor; }
    public int getPatch(){ return patch; }

    @Override
    public String toString(){
        return major + "." + minor + "." + patch;
    }
	
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Version)) return false;
        Version v = (Version) o;
        return major == v.major && minor == v.minor && patch == v.patch;
    }

    @Override
    public int hashCode(){
        return Objects.hash(major, minor, patch);
    }

    //===
}