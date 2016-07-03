package it.unimib.disco.summarization.experiments;

public class Concept {
	
	private String URI;
	private String name; //stringa che contiene solo il nome del Concetto
	//private int depth;
	
	public Concept(String URI){
		this.URI = URI;
		name = obtainName(URI);
	//	depth= 0;
	}
	
	public String getURI(){ return URI; }
	public void setURI(String arg) { URI = arg; } 
	
	public String getName(){ return name; }
	public void setName(String arg) { name = arg; } 
	
	//public int getDepth(){ return depth; }
	//public void setDepth(int arg) { depth = arg; } 
	
	private static String obtainName(String arg){
		int index = arg.lastIndexOf("/");
		return arg.substring(index + 1);
	}
	
	public String toString(){
		return URI;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((URI == null) ? 0 : URI.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Concept other = (Concept) obj;
		if (URI == null) {
			if (other.URI != null)
				return false;
		} else if (!URI.equals(other.URI))
			return false;
		return true;
	}

}