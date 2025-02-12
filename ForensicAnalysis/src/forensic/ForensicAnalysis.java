package forensic;

/**
 * This class represents a forensic analysis system that manages DNA data using
 * BSTs.
 * Contains methods to create, read, update, delete, and flag profiles.
 * 
 * @author Kal Pandit
 */
public class ForensicAnalysis {

    private TreeNode treeRoot; // BST's root
    private String firstUnknownSequence;
    private String secondUnknownSequence;

    public ForensicAnalysis() {
        treeRoot = null;
        firstUnknownSequence = null;
        secondUnknownSequence = null;
    }

    
    /**
     * Builds a simplified forensic analysis database as a BST and populates unknown
     * sequences.
     * The input file is formatted as follows:
     * 1. one line containing the number of people in the database, say p
     * 2. one line containing first unknown sequence
     * 3. one line containing second unknown sequence
     * 2. for each person (p), this method:
     * - reads the person's name
     * - calls buildSingleProfile to return a single profile.
     * - calls insertPerson on the profile built to insert into BST.
     * Use the BST insertion algorithm from class to insert.
     * 
     * DO NOT EDIT this method, IMPLEMENT buildSingleProfile and insertPerson.
     * 
     * @param filename the name of the file to read from
     */
    public void buildTree(String filename) {
        // DO NOT EDIT THIS CODE
        StdIn.setFile(filename); // DO NOT remove this line

        // Reads unknown sequences
        String sequence1 = StdIn.readLine();
        firstUnknownSequence = sequence1;
        String sequence2 = StdIn.readLine();
        secondUnknownSequence = sequence2;

        int numberOfPeople = Integer.parseInt(StdIn.readLine());

        for (int i = 0; i < numberOfPeople; i++) {
            // Reads name, count of STRs
            String fname = StdIn.readString();
            String lname = StdIn.readString();
            String fullName = lname + ", " + fname;
            // Calls buildSingleProfile to create
            Profile profileToAdd = createSingleProfile();
            // Calls insertPerson on that profile: inserts a key-value pair (name, profile)
            insertPerson(fullName, profileToAdd);
        }
    }

    /**
     * Reads ONE profile from input file and returns a new Profile.
     * Do not add a StdIn.setFile statement, that is done for you in buildTree.
     */
    public Profile createSingleProfile() {

        STR[] allSTRs = new STR[StdIn.readInt()];

        for (int i = 0; i < allSTRs.length; i++) {
            STR str = new STR(StdIn.readString(), StdIn.readInt());
            allSTRs[i] = str;

        }

        Profile finishedProfile = new Profile(allSTRs);

        return finishedProfile; // update this line
    }

    /**
     * Inserts a node with a new (key, value) pair into
     * the binary search tree rooted at treeRoot.
     * 
     * Names are the keys, Profiles are the values.
     * USE the compareTo method on keys.
     * 
     * @param newProfile the profile to be inserted
     */
    public void insertPerson(String name, Profile newProfile) {

        TreeNode newNode = new TreeNode(name, newProfile, null, null);

        if (treeRoot == null) {
            treeRoot = newNode;
        } else {
            TreeNode curr = treeRoot;

            while (curr != null) {
                if (name.compareTo(curr.getName()) < 0) {
                    if (curr.getLeft() == null) {
                        curr.setLeft(newNode);
                        break;

                    } else {
                        curr = curr.getLeft();
                    }
                } else {
                    if (curr.getRight() == null) {
                        curr.setRight(newNode);
                        break;
                    } else {
                        curr = curr.getRight();
                    }
                }
            }
        }

    }

    /**
     * Finds the number of profiles in the BST whose interest status matches
     * isOfInterest.
     *
     * @param isOfInterest the search mode: whether we are searching for unmarked or
     *                     marked profiles. true if yes, false otherwise
     * @return the number of profiles according to the search mode marked
     */
    public int getMatchingProfileCount(boolean isOfInterest) {

        return matchingC(treeRoot, isOfInterest);

    }

    private int matchingC(TreeNode root, boolean isOfInterest) {
        
        if (root == null) {
            
            return 0; 
        }
        
        int leftroot = matchingC(root.getLeft(), isOfInterest);
       
        int rightroot = matchingC(root.getRight(), isOfInterest);
        
      
        if ((root.getProfile().getMarkedStatus() && isOfInterest) || (!root.getProfile().getMarkedStatus() && !isOfInterest)) {
            
            return 1 + leftroot + rightroot; 

        } else {

            return leftroot + rightroot; 
        }
    }
    /**
     * Helper method that counts the # of STR occurrences in a sequence.
     * Provided method - DO NOT UPDATE.
     * 
     * @param sequence the sequence to search
     * @param STR      the STR to count occurrences of
     * @return the number of times STR appears in sequence
     */
    private int numberOfOccurrences(String sequence, String STR) {

        // DO NOT EDIT THIS CODE

        int repeats = 0;
        // STRs can't be greater than a sequence
        if (STR.length() > sequence.length())
            return 0;

        // indexOf returns the first index of STR in sequence, -1 if not found
        int lastOccurrence = sequence.indexOf(STR);

        while (lastOccurrence != -1) {
            repeats++;
            // Move start index beyond the last found occurrence
            lastOccurrence = sequence.indexOf(STR, lastOccurrence + STR.length());
        }
        return repeats;
    }

    /**
     * Traverses the BST at treeRoot to mark profiles if:
     * - For each STR in profile STRs: at least half of STR occurrences match (round
     * UP)
     * - If occurrences THROUGHOUT DNA (first + second sequence combined) matches
     * occurrences, add a match
     */

    

    

    public void flagProfilesOfInterest() {

        trav(treeRoot);

        
    
}

private void trav(TreeNode root){

    if(root != null){

        trav(root.getLeft());

        int countOfMatchingStrs = 0;

        STR[] strs = root.getProfile().getStrs(); 

        for(int i = 0; i < strs.length; i++){

            String strString = strs[i].getStrString();

            int strRepeatCount = numberOfOccurrences(firstUnknownSequence, strString) + numberOfOccurrences(secondUnknownSequence, strString);

            if(strRepeatCount == strs[i].getOccurrences()){
                
                countOfMatchingStrs++;
            }
        }

        if(countOfMatchingStrs >= Math.ceil(strs.length/2.0)){

            root.getProfile().setInterestStatus(true);
        }

        trav(root.getRight());
    }



}

    

    /**
     * Uses a level-order traversal to populate an array of unmarked Strings
     * representing unmarked people's names.
     * - USE the getMatchingProfileCount method to get the resulting array length.
     * - USE the provided Queue class to investigate a node and enqueue its
     * neighbors.
     * 
     * @return the array of unmarked people
     */
    public String[] getUnmarkedPeople() {

        int notMarked = getMatchingProfileCount(false); 
        String[] unmarkedP = new String[notMarked];
    
        Queue<TreeNode> queueP = new Queue<>();
        
        int index = 0; 

        if (treeRoot != null) {
            
            queueP.enqueue(treeRoot);
        }
    
        while (!queueP.isEmpty()) {
            
            TreeNode curr = queueP.dequeue();
            
            if (!curr.getProfile().getMarkedStatus()) {
                
                unmarkedP[index++] = curr.getName();
            }
            
            
            if (curr.getLeft() != null) {
                
                queueP.enqueue(curr.getLeft());
            }
            if (curr.getRight() != null) {
                
                queueP.enqueue(curr.getRight());
            }
        }
    
        return unmarkedP;
    
    }

    /**
     * Removes a SINGLE node from the BST rooted at treeRoot, given a full name
     * (Last, First)
     * This is similar to the BST delete we have seen in class.
     * 
     * If a profile containing fullName doesn't exist, do nothing.
     * You may assume that all names are distinct.
     * 
     * @param fullName the full name of the person to delete
     */
    public void removePerson(String fullName) {
        treeRoot = rPR(treeRoot, fullName);
        
    }

    private TreeNode rPR(TreeNode root, String name) {
        if(root == null) {

            return null;
        }
    
        int cmp = name.compareTo(root.getName());
        
        if(cmp < 0) {
            root.setLeft(rPR(root.getLeft(), name));

        }else if(cmp > 0){
            root.setRight(rPR(root.getRight(), name));
        }else{
           
            if(root.getLeft() == null && root.getRight() == null){

                return null;
            }else if(root.getLeft() == null){
               
                return root.getRight();
           
            }else if(root.getRight() == null){

                return root.getLeft();
          
            }else{

                TreeNode root2 = root;
                
                root = findMin(root2.getRight());

                root.setRight(removeMin(root2.getRight()));

                root.setLeft(root2.getLeft());
            }
        }

        return root;
    }
    
    private TreeNode findMin(TreeNode root) {

        while(root.getLeft() != null) {

            root = root.getLeft();
        }
        return root;
    }
    
    private TreeNode removeMin(TreeNode root) {

        if(root.getLeft() == null) {
        
            return root.getRight();
        }

        root.setLeft(removeMin(root.getLeft()));

        return root;
    }


    /**
     * Clean up the tree by using previously written methods to remove unmarked
     * profiles.
     * Requires the use of getUnmarkedPeople and removePerson.
     */
    public void cleanupTree() {
        
        String[] notMarked = getUnmarkedPeople(); 

        for (int i = 0; i < notMarked.length; i++) {

            String nameToUM = notMarked[i];

            removePerson(nameToUM);
        }

    }

    

    /**
     * Gets the root of the binary search tree.
     *
     * @return The root of the binary search tree.
     */
    public TreeNode getTreeRoot() {
        return treeRoot;
    }

    /**
     * Sets the root of the binary search tree.
     *
     * @param newRoot The new root of the binary search tree.
     */
    public void setTreeRoot(TreeNode newRoot) {
        treeRoot = newRoot;
    }

    /**
     * Gets the first unknown sequence.
     * 
     * @return the first unknown sequence.
     */
    public String getFirstUnknownSequence() {
        return firstUnknownSequence;
    }

    /**
     * Sets the first unknown sequence.
     * 
     * @param newFirst the value to set.
     */
    public void setFirstUnknownSequence(String newFirst) {
        firstUnknownSequence = newFirst;
    }

    /**
     * Gets the second unknown sequence.
     * 
     * @return the second unknown sequence.
     */
    public String getSecondUnknownSequence() {
        return secondUnknownSequence;
    }

    /**
     * Sets the second unknown sequence.
     * 
     * @param newSecond the value to set.
     */
    public void setSecondUnknownSequence(String newSecond) {
        secondUnknownSequence = newSecond;
    }

}
