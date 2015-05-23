package ch.hesge.csim2.engine.conceptmapper;

class TfIdfVector {

	// the index of the vector start from 1 like in math.
	//but the implementation uses an array so we must shift the index by -1
	private float[] contents;
	//thsi represents the "document" in which the terms are found. This is a method or concept.
	private StemLocationElement document;

	StemLocationElement getSource() {
		return document;
	}

	TfIdfVector(int size, StemLocationElement location) {
		contents = new float[size];
		for (int i = 0; i < size; i++)
			contents[i] = 0;
		this.document = location;
	}

	TfIdfVector(int size) {
		this(size, null);
	}

	int size() {
		return contents.length;
	}

	//first position in a vector is 1, not 0 like in the primitive array.
	boolean put(int index, float value) {
		if (index > size())
			return false;
		if (index < 1)
			return false;
		contents[index - 1] = value;
		return true;
	}

	boolean put(int index, double value) {
		if (index > size())
			return false;
		if (index < 1)
			return false;
		contents[index - 1] = (float) value;
		return true;
	}

	boolean put(int index, int value) {
		if (index > size())
			return false;
		if (index < 1)
			return false;
		contents[index - 1] = (float) value;
		return true;
	}

	boolean increment(int index) {
		if (index > size())
			return false;
		if (index < 1)
			return false;
		contents[index - 1] = contents[index - 1] + 1;
		return true;
	}

	//first position in a vector is 1, not 0 like in the primitive array.
	float get(int index) {
		if (index > size())
			return -1;
		if (index < 1)
			return -1;
		return contents[index - 1];
	}

	// returns a new vector that corresponds to the hadamard product 
	//of this and the parameter vector
	TfIdfVector hardamardProduct(TfIdfVector vect) throws Exception {
		if (size() != vect.size())
			throw new Exception("vectors of different sizes");
		if (document != null && vect.getSource() != null)
			throw new Exception("incompatible sourceIds");
		StemLocationElement loc;
		if (document == null)
			loc = vect.getSource();
		else
			loc = document;
		TfIdfVector result = new TfIdfVector(size(), loc);
		//index must go from 1 to the vector size
		for (int i = 1; i <= size(); i++)
			result.put(i, get(i) * vect.get(i));
		return result;
	}

	// returns a float that corresponds to the dot product of this and the parameter vector
	float dotProduct(TfIdfVector vect) throws Exception {
		if (size() != vect.size())
			throw new Exception("vectors of different sizes");
		float result = 0;
		for (int i = 1; i <= size(); i++)
			result = result + (get(i) * vect.get(i));
		return result;
	}

	// returns a float that corresponds to the length of the vector
	float length() {
		float result = 0;
		for (int i = 0; i < size(); i++)
			result = result + (contents[i] * contents[i]);
		return (float) Math.sqrt(result);
	}

	// returns a float that corresponds to the length of the vector
	int elementsNotNull() {
		int result = 0;
		for (int i = 0; i < size(); i++)
			if (contents[i] != 0.0)
				result++;
		return result;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("TFIDF vector \n");
		for (int i = 0; i < size(); i++) {
			sb.append(i);
			sb.append(" -> ");
			sb.append(contents[i]);
			sb.append("\n");
		}
		return sb.toString();

	}
}
