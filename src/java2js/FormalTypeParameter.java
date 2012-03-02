package java2js;

import java.util.*;

public class FormalTypeParameter {
    private final String identifier;
    private final TypeSignature classBound;
    private final List<TypeSignature> interfaceBounds = new ArrayList<TypeSignature>();
    
    public FormalTypeParameter(String _identifier, 
			       TypeSignature _classBound,
			       List<? extends TypeSignature> _interfaceBounds) {
	this.identifier = _identifier;
	this.classBound = _classBound;
	this.interfaceBounds.addAll(_interfaceBounds);
    }

    public String getIdentifier() {return this.identifier;}
    public TypeSignature getClassBound() {return this.classBound;}
    public int getNumInterfaceBounds() {return this.interfaceBounds.size();}
    public TypeSignature getInterfaceBound(int i) {return this.interfaceBounds.get(i);}
}
