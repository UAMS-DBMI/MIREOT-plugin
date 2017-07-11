# MIREOT Plugin 

This plugin performs the MIREOT plugin through Protege via a convinent drag and drop interface.

The current version can be downloaded on the [Release](https://github.com/UAMS-DBMI/MIREOT-plugin/releases) page of this repository.



#### Development Prerequisites

If you need to build this plugin the following is required.

+ Apache's [Maven](http://maven.apache.org/index.html).
+ A tool for checking out a [Git](http://git-scm.com/) repository.
+ A Protege distribution (5.0 or higher).  The Protege 5.0.0 releases are [available](http://protege.stanford.edu/products.php#desktop-protege) from the main Protege website. 

#### Build and install of plug-ins

1. Get a copy of this code:

        git clone https://github.com/UAMS-DBMI/MIREOT-plugin.git 
    
2. Change into the MIREOT-plugin directory.

3. Type `mvn clean package`.  On build completion, the "target" directory will contain a protege.plugin.mireot-${version}.jar file.

4. Copy the JAR file from the target directory to the "plugins" subdirectory of your Protege distribution.
 
#### Questions

If you have questions about this MIREOT Protege plug-ins please send messages to jrutecht at uams.edu.

