# GitLib

Test task for Jetbrains internship.

## Usage

### Public classes:
* GitLib:
    Main class, which provides all functionality.
    * Constructor initializes object at specified path, similar to git init.
    * Functions:
       * add(String filename) adds file to stage, similar to git add. Provided filename should be path to file relative
      to root directory.  
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

* Tree
* UnfinishedTree
* TreeBuilder
* BlobPair
* TreePair
* GitObject
