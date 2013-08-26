package fursten.simulator.node;

import fursten.simulator.node.Node;

public class NodeKeyGenerator {

	private static final String[] charTable = new String[]{
		"0","1","2","3","4","5","6","7","8","9",
		"a","b","c","d","e","f","g","h","i","j",
		"k","l","m","n","o","p","q","r","s","t",
		"u","v","w","x","y","z","A","B","C","D",
		"E","F","G","H","I","J","K","L","M","N",
		"O","P","Q","R","S","T","U","V","W","X",
		"Y","Z","-","+"
	};
	
	public static String generateKey(Node parentNode, Node childNode) {
		return generateKey(parentNode.getX(), parentNode.getY(), parentNode.getR(), childNode.getX(), childNode.getY(), childNode.getR());
	}
	
	public static String generateKey(Node node) {
		return generateKey(node.getX(), node.getY(), node.getR());
	}
	
	public static String generateKey(int x1, int y1, int r1, int x2, int y2, int r2) {
		return generateKey(x1, y1, r1) + generateKey(x2, y2, r2);
	}
	
	public static String generateKey(int x, int y, int r) {
		
		StringBuilder strBuilder = new StringBuilder();
		
		//Append X
		for(int i=0; i < 6; i++) {
			int v = (x >> i*6) & 63;
			strBuilder.append(charTable[v]);
		}
		//Append Y
		for(int i=0; i < 6; i++) {
			int v = (y >> i*6) & 63;
			strBuilder.append(charTable[v]);
		}
		//Append R
		for(int i=0; i < 6; i++) {
			int v = (r >> i*6) & 63;
			strBuilder.append(charTable[v]);
		}
		
		return strBuilder.toString();
	}
}


