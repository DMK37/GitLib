# GitLib

Test task for Jetbrains internship.

Some example usage can be found in tests.

## Usage

### Public classes:
* GitLib:
    Main class, which provides all functionality.
    * Constructor initializes object at specified path, similar to git init.
    * Functions:
       * add(String filename) adds file to stage, similar to git add. Provided filename should be path to file relative
      to root directory. Also, as filename can be provided *. 
       * commit(String author, String message) creates commit with specified message, similar to git commit.
       * listCommits() returns list of all commits in repository and prints them.
       * findCommitByHash(String hash) returns commit with specified hash.
       * findCommitByMetadata(String author, String message, LocalDateTime time, String parentHash, String treeHash) 
      returns commit with specified metadata.
       * showObjectByHash(String hash) prints blob or tree with specified hash.
* Commit:
    Class, which represents commit.
    * Constructor not intended for public use.
    * Functions:
       * getHash() returns hash of commit.
       * getAuthor() returns author of commit.
       * getMessage() returns message of commit.
       * getTime() returns time of commit.
       * getParentHash() returns hash of parent commit.
       * getTreeHash() returns hash of tree.
### Package private classes:
* Blob:
    Class represents blob.
    * Constructor requires data as string.
    * Functions:
       * getHash() returns hash of blob.
       * getData() returns data of blob.
* Tree:
    Class represents tree.
    * Constructor requires list of TreePairs and list of BlobPairs.
    * Functions:
       * getHash() returns hash of tree.
       * getBlobs() returns list of BlobPair.
       * getTrees() returns list of TreePair.
* UnfinishedTree:
    Class represents unfinished tree to which we can add other unfinished trees or blobs.
    * In constructor, we can specify list of TreePairs and list of BlobPairs and filename.
    * Functions:
       * addBlob(Blob blob) adds blob to unfinished tree.
       * addUnfinishedTree(UnfinishedTree unfinishedTree) adds unfinished tree to unfinished tree.
       * build(HashMap<String, GitObject> objects) builds tree and returns it, if unfinished tree contains other 
        unfinished trees, they will be built recursively too. Passing **objects** hashmap is necessary to not creating again blobs 
      and trees which are already created.
      * TreeBuilder:
          Contains all unfinished trees in hashmap, where key is path to folder from Gitlib root.
      * Functions:
           * addBlob(Blob blob, String filename) adds blob to proper unfinished tree.
           * build(HashMap<String, GitObject> objects) adds missing unfinished trees, for example if we have blobs at 
           src/main/java we also need to create trees src and src/main. After calls build on root unfinished tree 
          and returns tree.
* BlobPair:
    Record, which represents a pair of filename and blob.
* TreePair:
    Record, which represents a pair of filename and tree.
* GitObject:
    Abstract class, which groups blob and tree.
