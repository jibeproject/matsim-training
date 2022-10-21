# matsim-training

Repository for the MATSim Training Session for JIBE on 31 October 2022, Valencia, Spain.

It is based largely on the default MATSim example project (matsim-org/matsim-example-project)

In this training, we will use two tools: 
1. Integrated development environment (IDE) for Java program (IntelliJ or Eclipse)
2. VIA for visualizing MATSim results

Below are the guides of tool installation and setups. Please read the guides and set up your devices. We will help on setups during the training sessions, but it is recommended to do it beforehand in order to save training time and avoid inability/slow to download due to the internet issue. 


### Install IntelliJ 
1. IntelliJ (community edition) for Windows & Mac can be found here: https://www.jetbrains.com/idea/download/ 
2. Please find the installation file and follow the installation wizard to install IntelliJ.
3. After installation, open IntelliJ and then follow the steps below "Import into IntelliJ" to import the MATSim training project.


### Java Version
The project uses Java 17. Usually a suitable SDK is packaged within IntelliJ or Eclipse. Otherwise, one must install a 
suitable sdk manually, which is available [here](https://openjdk.java.net/)


### Install VIA
1. VIA for Windows & Mac can be found here: https://simunto.com/via/download
2. Apply for VIA free license here: https://simunto.com/via/licenses/free


### Import into IntelliJ (recommended)

`File -> New -> Project from Version Control` paste the repository url and hit 'clone'. IntelliJ usually figures out
that the project is a maven project. If not: `Right click on pom.xml -> import as maven project`.

  
### Import into eclipse

1. download a modern version of eclipse. This should have maven and git included by default.
1. `file->import->git->projects from git->clone URI` and clone as specified above.  _It will go through a 
sequence of windows; it is important that you import as 'general project'._
1. `file->import->maven->existing maven projects`

Sometimes, step 3 does not work, in particular after previously failed attempts.  Sometimes, it is possible to
right-click to `configure->convert to maven project`.  If that fails, the best thing seems to remove all 
pieces of the failed attempt in the directory and start over.







### Licenses
The **MATSim program code** in this repository is distributed under the terms of the [GNU General Public License as published by the Free Software Foundation (version 2)](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html). The MATSim program code are files that reside in the `src` directory hierarchy and typically end with `*.java`.
