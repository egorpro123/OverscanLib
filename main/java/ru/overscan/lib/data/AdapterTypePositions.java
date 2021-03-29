package ru.overscan.lib.data;

import android.util.Log;

import java.util.ArrayList;

public class AdapterTypePositions {
	final static String TAG = "AdapterTypePositions";

	ArrayList<Type> positions;
//	int counter;
	
	class Type {
		int code;
//		int firstPos;
		int lastPos;
		
		Type(int code, int lastPos){
			this.code = code;
			this.lastPos = lastPos;
		}
	}

    class TypeWithPosition{
        int type;
        int pos;
    }
	
	public AdapterTypePositions() {
		positions = new ArrayList<Type>();
//		counter = 0;
	}

    // должны добавляться по порядку
    // порядок элементов внутри типа д.б.от 0,1,2,..., если порядок другой
    //   не используйте getPositionInType(int position)
	public void add(int type, int quantity) {
		int prev;
		if (positions.size() == 0) prev = -1;
		else prev = positions.get(positions.size() - 1).lastPos;
//		Log.d(TAG, "positions.size() " + Integer.toString(positions.size()));
//        Type t = new Type(type, prev + quantity);
		positions.add(positions.size(), new Type(type, prev + quantity));
//		counter++;
	}
	
	public int getTypeAtPosition(int position){
		Type t;
		for (int i = 0; i < positions.size(); i++) {
			t = positions.get(i);
			if (position <= t.lastPos) return t.code; 
		}
		return -1;
	}

    private TypeWithPosition getTypeWithPosition(int position){
        Type t;
        for (int i = 0; i < positions.size(); i++) {
            t = positions.get(i);
            if (position <= t.lastPos) {
                TypeWithPosition twp = new TypeWithPosition();
                twp.type = t.code;
                twp.pos = i;
                return twp;
            }
        }
        return null;
    }

	public int getTypeCount() {
		return positions.size();
		
	}
	
	public int getPositionsCount() {
		if (positions.size() == 0) return 0;
		return positions.get(positions.size() -1).lastPos + 1;
	}

	public int getPositionInType(int position) {
        TypeWithPosition twp = getTypeWithPosition(position);
        if (twp == null) return -1;
        if (twp.pos == 0) return position;
        else {
            Type t = positions.get(twp.pos - 1);
                return position - t.lastPos - 1;
        }


	}
	
}
