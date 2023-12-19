package RBTree;

/*
 * Program 1: Red-Black Trees
 * CS 223 - 01
 * Professor Wallace
 * Elijah Delavar
 */

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Random;
import java.util.Vector;


public class RBSymbolTable<K extends Comparable<K>, V>
        implements SymbolTable<K, V> {

	private enum Color {BLACK, RED};
	
    private class Node {
        public K key;
        public V val;
        public Node left, right;
        public Color col;

        public Node(K k, V v) {
            key = k;
            val = v;
            left = right = null;
            col = Color.RED; // All new nodes are red per Red-Black Tree rules
        }
    }

    private Node root;

    public RBSymbolTable() {
        root = null;
    }

    /**
     * Walk down the tree.
     * Split four nodes and resolve color conflicts using rotations on the way down.
     * If node found with same key, update its value.
     * Else, Insert node with key and val pair at leaf.
     * Resolve color conflicts using rotations.
     *
     * @param key Key used to later retrieve the data
     *            Must not be null
     *            Duplicate keys replace old data
     * @param val Value associated with the key
     *            May be null
     */
    public void insert(K key, V val) {
    	if (key == null) return;
    	System.out.println(key + "\n");
    	
    	Node toinsert = new Node(key, val);
    	
    	if (root == null) {
    		root = toinsert;
    		root.col = Color.BLACK;
    		return;
    	}
    	
    	// Loop to find where to insert
    	Node gggp = null;	// Great great grandparent
    	Node ggp = null;	// Great grandparent
    	Node gp = null;		// Grandparent
    	Node p = root;		// Parent
    	while (p != toinsert) {
    		// Check for four node
    		if (p.left != null && p.right != null 
    				&& p.left.col == Color.RED && p.right.col == Color.RED) {
    			// Four node found
    			// Change colors of children to black and parent to red (root goes to black)
    			p.left.col = Color.BLACK;
    			p.right.col = Color.BLACK;
    			
    			if (p != root) {
    				p.col = Color.RED;
    				
    				// Check to see if grandparent is red 
    				if (gp.col == Color.RED) {
    					// Error: parent node is red and grandparent is red
    					Node newroot = null;
    					
    					// Rotations
    					if (p.key.compareTo(gp.key) < 0) {
    						// p < gp
    						if (gp.key.compareTo(ggp.key) < 0) {
	    						// gp < ggp
	    						// Right rotate
	    						newroot = rotateRight(ggp);
    						}else {
    							// gp > ggp
    							// Right rotate, left rotate
    							ggp.right = rotateRight(gp);
    							newroot = rotateLeft(ggp);
    						}
    						
    						// Attach newroot to great grandparent
    						if (ggp == root) root = newroot;
    						else {
    							if (newroot.key.compareTo(gggp.key) < 0) gggp.left = newroot;
    							else gggp.right = newroot;
    						}
    						
    						// Recolor
    						newroot.col = Color.BLACK;
    						newroot.right.col = Color.RED;
    						newroot.left.col = Color.RED;
    					}else {
    						// p > gp
    						if (gp.key.compareTo(ggp.key) < 0) {
	    						// gp < ggp
	    						// Right rotate
    							ggp.left = rotateLeft(gp);
    							newroot = rotateRight(ggp);
    						}else {
    							// gp > ggp
    							// Right rotate, left rotate
    							newroot = rotateLeft(ggp);
    						}
    						
    						// Attach newroot to great great grandparent
    						if (ggp == root) root = newroot;
    						else {
    							if (newroot.key.compareTo(gggp.key) < 0) gggp.left = newroot;
    							else gggp.right = newroot;
    						}
    						
    						// Recolor
    						newroot.col = Color.BLACK;
    						newroot.right.col = Color.RED;
    						newroot.left.col = Color.RED;
    					}
    				}
    			}
    		}
    		
    		// Check if parent key is equal to search key
    		if (toinsert.key == p.key) {
    			// Update value of existing node
    			p.val = toinsert.val;
    			return;
    		}
    		
    		// Check whether search key (n) is less than or greater than parent key (p)
    		else if (toinsert.key.compareTo(p.key) < 0) {
    			// toinsert < p
    			if (p.left == null) {
    				// Insert here
    				p.left = toinsert;
    				
    				if (p.col == Color.RED) {
    					// Error: new node is red and parent is red.
    					Node newroot = null;

    					// Rotations
    					if (p.key.compareTo(gp.key) < 0) {
    						// p < gp
    						// Right rotate
    						newroot = rotateRight(gp);
    					}else {
    						// p > gp
    						// Right rotate, left rotate
    						gp.right = rotateRight(p);
    						newroot = rotateLeft(gp);
    					}

    					// Attach newroot to great grandparent
						if (gp == root) root = newroot;
						else {
							if (newroot.key.compareTo(ggp.key) < 0) ggp.left = newroot;
							else ggp.right = newroot;
						}
    					
    					// Recolor
    					newroot.col = Color.BLACK;
    					newroot.right.col = Color.RED;
    					newroot.left.col = Color.RED;
    					return;
        			}
    			}
    			gggp = ggp;
    			ggp = gp;
    			gp = p;
    			p = p.left;
    		}
    		else {
    			// toinsert > p
    			if (p.right == null) {
    				// Insert here
    				p.right = toinsert;
    				
    				if (p.col == Color.RED) {
        				// Error: new node is red and parent is red.
    					Node newroot = null;

    					// Rotations
    					if (p.key.compareTo(gp.key) < 0) {
    						// p < gp
    						// Left rotate, right rotate
    						gp.left = rotateLeft(p);
    						newroot = rotateRight(gp);
    					}else {
    						// p > gp
    						// Left rotate
    						newroot = rotateLeft(gp);
    					}
    					
    					// Attach newroot to great grandparent
						if (gp == root) root = newroot;
						else {
							if (newroot.key.compareTo(ggp.key) < 0) ggp.left = newroot;
							else ggp.right = newroot;
						}
    					
    					// Recolor
    					newroot.col = Color.BLACK;
    					newroot.right.col = Color.RED;
    					newroot.left.col = Color.RED;
    					return;
        			}
    			}
    			gggp = ggp;
    			ggp = gp;
    			gp = p;
    			p = p.right;
    		}
    	}
    }
    
    /**
     * Retrieve the value associated with the given key, if present
     * <p>
     * Implementation of the search method in the interface.
     * Again, we just call a recursive helper.
     *
     * @param key key whose value we'd like to retrieve
     *            may not be null
     * @return value associated with key (may be null) or null if key is absent
     */
    public V search(K key) {
        return searchHelper(root, key);
    }

    
    
    /**
     * Internal helper for the search method
     * <p>
     * Recursively search tree rooted at tree for given key
     * Returns the associated value, if it exists, or null
     * if the key is not found.
     * <p>
     * Note that as a result of the way this works, we can't
     * have a key whose value is null
     *
     * @param tree root node of the (sub-)tree to search
     * @param key  see notes for search()
     * @return value associated with key (may be null), or null if key is absent
     */
    private V searchHelper(Node tree, K key) {
        if (tree == null) {
            // tree is empty or no more tree, so key isn't here
            return null;
        }
        
        int cmp = key.compareTo(tree.key);
        
        if (cmp == 0) {
            // found the key, return its value
            return tree.val;
        }
        
        return (cmp < 0) ? searchHelper(tree.left, key) : searchHelper(tree.right, key);
    }
    
    
    
    // DO NOT TOUCH anything (except Main method) below this line!
    
    
    
    /**
     * Right Rotation
     * <p>
     * Performs a right rotation of the designated node.
     * This is pulled directly from the slides, so you don't have to.
     *
     * @param tree node to be rotated
     *             tree must exist, as must tree.left
     * @return root of tree post-rotation (should be the original tree.left)
     */
    private Node rotateRight(Node tree) {
        Node root = tree.left;
        tree.left = root.right;
        root.right = tree;
        return root;
    }

    /**
     * Left Rotation
     * <p>
     * Performs a left rotation of the designated node.
     * This is pull directly from the slides, so you don't have to.
     *
     * @param tree node to be rotated
     *             tree must exist, as must tree.right
     * @return root of tree post-rotation (should be the original tree.right)
     */
    private Node rotateLeft(Node tree) {
        Node root = tree.right;
        tree.right = root.left;
        root.left = tree;
        return root;
    }
    
    
    
    /**
     * Serialize tree into a vector for use with support functionality
     * <p>
     * This method is not part of the symbol table interface
     * Instead, it lets us convert the RBSymbolTable into a
     * form that's easy to hand off for display or testing.
     * This works by traversing the tree (with a helper) and
     * shoving information about its nodes into a vector we
     * can pass off later.
     *
     * @return Vector of strings containing key and node color (always black)
     * nodes are presented in preorder traversal order
     * null objects are used to indicate an absent child
     */
    public Vector<String> serialize() {
        Vector<String> vec = new Vector<>();
        serializeHelper(root, vec);
        return vec;
    }

    
    
    /**
     * Recursive helper for serialization
     * <p>
     * Perform a (recursive) pre-order traversal and
     * store node information into a provided vector of strings.
     * Note that we add ":black" to the end of the node's key.
     * This is because the TreePrinter will happily work on
     * Red-Black trees, where color is significant, so we fill
     * in a default.
     *
     * @param tree root node of (sub-)tree to serialize
     * @param vec  vector object into which to serialize
     */
    private void serializeHelper(Node tree, Vector<String> vec) {
        String nodeColor = ":black";
        if (tree == null)
            vec.addElement(null);
        else {
        	if (tree.col == Color.RED) nodeColor = ":red";
            vec.addElement(tree.key.toString() + nodeColor);
            serializeHelper(tree.left, vec);
            serializeHelper(tree.right, vec);
        }
    }

    

    /**
     * Use TreePrinter class to generate picture of the tree
     * <p>
     * This interacts with the TreePrinter class for us.
     * First, we generate a vector of strings containing the
     * serialized tree. Once that's done, use it to create
     * a TreePrinter object, and then open a file and have
     * the TreePrinter throw a picture of the tree into the file.
     *
     * @param fname name of file to output.
     *              should end in .svg
     */
    private void printTree(String fname) {
        Vector<String> st = serialize();
        TreePrinter treePrinter = new TreePrinter(st);
        treePrinter.fontSize = 14;
        treePrinter.nodeRadius = 14;
        try {
            FileOutputStream out = new FileOutputStream(fname);
            PrintStream ps = new PrintStream(out);
            treePrinter.printSVG(ps);
        } catch (FileNotFoundException e) {
        }
    }

    /*
     * This main provides a relatively simple test harness for
     * the RBSymbolTable, by randomly adding some nodes and
     * then invoking the TreePrinter to get a picture of the tree.
     */
    public static void main(String args[]) {
        RBSymbolTable<Integer, Integer> symtab = new RBSymbolTable<>();

        symtab = new RBSymbolTable<>();
        Random RNG = new Random(1234);
        for (int i = 0; i < 100; i++) {
            int r = (int) (RNG.nextDouble() * 100);
            symtab.insert(r, r);
        }

        symtab.printTree("randomtree.svg");
    }
}
