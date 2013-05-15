package fursten.simulator.persistent.mysql;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fursten.simulator.node.Node;
import fursten.simulator.node.Nodes;

public class NodeTree implements Serializable  {
	
	static final long serialVersionUID = 10275539472837495L;
    private QNode root;
    
    static class QNode implements Serializable {
    	
    	static final long serialVersionUID = 10275539472837495L;
    	int x, y, d;
    	QNode NW, NE, SE, SW;
        Node node;

        QNode(int x, int y, int d, Node node) {
            this.x = x;
            this.y = y;
            this.d = d;
            this.node = node;
        }
        
        /*public boolean intersect(int xmin, int ymin, int xmax, int ymax) {
        	
        	if(x - d > xmax) return false;
        	else if(x + d < xmin) return false;
        	else if(y - d > ymax) return false;
        	else if(y + d < ymin) return false;
        	else return true;
        }*/
        
        public boolean intersect(Rectangle rect) {
     	   
        	if(x - d > rect.getMaxX()) return false;
        	else if(x + d < rect.getMinX()) return false;
        	else if(y - d > rect.getMaxY()) return false;
        	else if(y + d < rect.getMinY()) return false;
        	else return true;
        }
        
        public QNode getSubCell(int x, int y) {
        	
        	if (x < this.x && y < this.y) {
        		return SW;
        	}
        	else if (x < this.x && y >= this.y) {
            	return NW;
            }
        	else if (x >= this.x && y < this.y) {
            	return SE;
            }
        	else if (x >= this.x && y >= this.y) {
            	return NE;
            }
        	else {
        		return null;
        	}
            
        }
    }
    
    public NodeTree(int logDim) {
    	
    	//Must be based on base 2
    	int dim = (int)Math.pow(2, logDim);
    	root = new QNode(0, 0, dim, null);
    }
    
   /***********************************************************************
    *  Insert (x, y) into appropriate quadrant
    ***********************************************************************/
    public boolean insert(Node node) {
    	
    	if(!(Math.abs(node.getX()) < root.d && Math.abs(node.getY()) < root.d)) {
    		System.out.println("Not within bounds " + Math.abs(node.getX()) + ":" + root.d + " # " + Math.abs(node.getY()) + ":" + root.d);
    		return false;
    	}
    	
    	QNode parent = insert(root, node);
    	return true;
    }

    private QNode insert(QNode h, Node node) {
        
    	//Add to cell
    	if (h.node == null) {
    		h.node = node;
    		return h;
    	}
    	else if(Nodes.intersect(h.node, node)) {
    		h.node = Nodes.add(h.node, node);
    		return h;
    	}
    	
    	//Add to subcell
        if (node.getX() < h.x && node.getY() < h.y) {
        	
        	if(h.SW == null) {
        		h.SW = new QNode(h.x - h.d/2, h.y - h.d/2, h.d/2, node);
        		return h.SW;
        	}
        	else {
        		return insert(h.SW, node);
        	}
        }
        else if (node.getX() < h.x && node.getY() >= h.y) {
        	
        	if(h.NW == null) {
        		h.NW = new QNode(h.x - h.d/2, h.y + h.d/2, h.d/2, node);
        		return h.NW;
        	}
        	else {
        		return insert(h.NW, node);
        	}
        }
        else if (node.getX() >= h.x && node.getY() < h.y) {
        	
        	if(h.SE == null) {
        		h.SE = new QNode(h.x + h.d/2, h.y - h.d/2, h.d/2, node);
        		return h.SE;
        	}
        	else {
        		return insert(h.SE, node);
        	}
        }
        else if (node.getX() >= h.x && node.getY() >= h.y) {
        	
        	if(h.NE == null) {
        		h.NE = new QNode(h.x + h.d/2, h.y + h.d/2, h.d/2, node);
        		return h.NE;
        	}
        	else {
        		return insert(h.NE, node);
        	}
        }
    	
    	//Only if something went wrong
    	System.out.println("#something went wrong ");
        return null;
    }

    /***********************************************************************
     *  Delete node
     ***********************************************************************/
     public boolean delete(Node node) {
         return delete(root, node);
     }

     private boolean delete(QNode h, Node node) {
    	 
    	 if(h == null){
    		 return false;
    	 }
    	 else if(h.node != null) {
    		 
    		//Substract the deleted amount
    		if(Nodes.intersect(h.node, node)) {
    	    	h.node = Nodes.substract(h.node, node);
    	    	if(h.node.getV() > 0) {
    	    		return true;
    	    	}
    	    	else {
    	    		h.node = null;
    	    		return true;
    	    	}
    	    }
    	 }
    	 
    	 /*else if(node.equals(h.node)) {
    		 h.node = null;
    		 return true;
    	 }*/
     
    	 QNode subCell = h.getSubCell(node.getX(), node.getY());
    	 return delete(subCell, node);
     }

  /***********************************************************************
    *  Range search.
    ***********************************************************************/

    public List<Node> get(Rectangle bounds) {
    	
    	ArrayList<Node> nodes = new ArrayList<Node>();
    	query(root, bounds, nodes);
    	return nodes;
    }

    private void query(QNode h, Rectangle b, List<Node> nodes) {
    	
    	if(h == null) {
    		return;
    	}
    	else if(!h.intersect(b)) {
    		return;
    	}
    	
    	if(h.node != null) {
    		//if(b.getMinX() <= h.node.getX() && b.getMaxX() >= h.node.getX() && b.getMinY() <= h.node.getY() && b.getMaxY() >= h.node.getY())
    		if(b.contains(h.node.getX(), h.node.getY()))
    			nodes.add(h.node);
    	}
    	
    	query(h.NE, b, nodes);
    	query(h.NW, b, nodes);
    	query(h.SE, b, nodes);
    	query(h.SW, b, nodes);
    }

}
