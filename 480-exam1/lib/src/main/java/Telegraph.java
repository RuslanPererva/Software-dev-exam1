public class Telegraph {
	
    private String Message;
	

	public Telegraph() {
        this.Message = "";
    }
	
	public Telegraph start() {
		this.Message = "";
		return this;
	}
	
	public Telegraph dot(int count) {
        for (int i = 0; i < count; i++) {
            this.Message+=".";
        }
        return this;
    }
	public Telegraph dash(int count) {
        for (int i = 0; i < count; i++) {
            this.Message+="-";
        }
        return this;
	}
	public Telegraph gap() {
            this.Message+=" ";

        return this;
	}
	
	public Telegraph word() {
        this.Message+="   ";

    return this;
	}
	
	public String end() {
		return Message.trim();
	}
}
