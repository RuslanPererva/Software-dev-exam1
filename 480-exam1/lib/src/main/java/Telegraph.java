public class Telegraph {
	
    private String Message;
	

	public Telegraph() {
        Message = "";
    }
	
	public Telegraph start() {
		Message = "";
		return this;
	}
	
	public Telegraph dot(int count) {
        for (int i = 0; i < count; i++) {
            Message+=".";
        }
        return this;
    }
	public Telegraph dash(int count) {
        for (int i = 0; i < count; i++) {
            Message+="-";
        }
        return this;
	}
	public Telegraph gap() {
            Message+=" ";

        return this;
	}
	
	public Telegraph word() {
        Message+="   ";

    return this;
	}
	
	public String end() {
		return Message;
	}
}
