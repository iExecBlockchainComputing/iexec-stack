<?php
include "entete.html";
?>


<div class="droite">

<ul class="menuprincipal">
<li> <a href="#CORRELIZER">DNA correlizer</a>
<li> <a href="#DART">DART : A Framework for Distributed Audio Analysis and Music Information Retrieva</a>
<li> <a href="#MATLAB">Porting Multiparametric MATLAB Application for Image and Video Processing to Desktop Grid for High-Performance Distributed Computing</a>
<li> <a href="#MATLAB2">Kinetics of Defect Aggregation in Materials Science Simulated in Desktop Grid Computing Environment Installed in Ordinary Material Science Lab</a>
</ul>

<hr />

<a name="CORRELIZER" />

  <p class="titre_app">DNA Correlation</p>
  <p class="author">A. Abuseiris, Erasmus - NL</p>

<div class="abstract">

The sequential organization of genomes, i.e. the relations between distant base pairs and regions within sequences, and its connection to the three-dimensional organization of genomes is still a largely unresolved problem. Long-range power-law correlations were found using correlation analysis on almost the entire observable scale of 132 completely sequenced chromosomes of 0.5 × 106 to 3.0 × 107 bp from Archaea, Bacteria, Arabidopsis thaliana, Saccharomyces cerevisiae, Schizosaccharomyces pombe, Drosophila melanogaster, and Homo sapiens. The local correlation coefficients show a species-specific multi-scaling behaviour: close to random correlations on the scale of a few base pairs, a first maximum from 40 to 3,400 bp (for Arabidopsis thaliana and Drosophila melanogaster divided in two submaxima), and often a region of one or more second maxima from 105 to 3 × 105 bp. Within this multi-scaling behaviour, an additional fine-structure is present and attributable to codon usage in all except the human sequences, where it is related to nucleosomal binding. Computer-generated random sequences assuming a block organization of genomes, the codon usage, and nucleosomal binding explain these results. Mutation by sequence reshuffling destroyed all correlations. Thus, the stability of correlations seems to be evolutionarily tightly controlled and connected to the spatial genome organization, especially on large scales. In summary, genomes show a complex sequential organization related closely to their three-dimensional organization.

<p class="keywords">
Keywords: Genome organization, Nuclear architecture, Long-range correlations, Scaling analysis, DNA sequence classification
<p>
<img width="570px" src="images/dna.jpg" />
</div>

<hr />

<a name="DART" />

  <p  class="titre_app">DART</p>
  <p class="author">E. Al-Shakarchi, Cardiff University - UK</p>

<div class="abstract">

Audio analysis algorithms and frameworks for Music Information Retrieval (MIR) are expanding rapidly, providing new ways to garnish non-trivial information from audio sources, beyond that which can be ascertained from unreliable metadata such as ID3 tags. The analysis component of MIR requires extensive computational resources. MIR is a broad field, and many aspects of the algorithms and analysis components that are used are more accurate given a larger dataset for analysis, and is often quite DSP/CPU intensive. A Desktop Grid based implementation would reduce computation time and provide access to potentially thousands of MP3 files on target machines, where the files analysed locally on clients’ machines, transferring back only the metadata/results of the analysis. This avoids legal issues, and saves bandwidth.


The DART application framework developed at Cardiff University focuses on the analysis of audio, with a particular interest into MIR. The existing application is designed and created in Triana, a graphical workflow-design environment that is used as a development test bed for the algorithms that will be distributed. The algorithms are programmed in a modular way, which allows only the relevant building blocks of the workflow to be converted into the standalone DART Java application, which is in turn converted into a JAR (multi-platform) executable, and is distributed to target machines across the Desktop Grid using BOINC or XtremWeb.

<img width="570px" src="images/dart.png" />
</div>


<hr />

<a name="MATLAB" />

  <p  class="titre_app">Porting Multiparametric MATLAB Application for Image and Video Processing to Desktop Grid for High-Performance Distributed Computing</p>
  <p class="author">Y. Gordienko, Institut de Physique du Metal - Kiev - Ukraine</p>

<div class="abstract">

Optical microscopy is usually used for structural characterization of materials in narrow ranges of magnification, small region of interest (ROI), and in static regime. But many crucial processes of damage initiation and propagation take place dynamically in the wide observable time domain from 10-3 s to 103 s and on the many scales from 10-8 m (solitary defects places) to 10-2 m (correlated linked network of defects). We used one of them to observe in real-time regime the dynamic behavior of the material under mechanical deformation in loading machine, record its evolution, and apply our multiscale image processing software (MultiscaleIVideoP). Our calculations include many parameters of physical process (process rate, magnification, illumination conditions, hardware filters, etc.) and image processing parameters (size distribution, anisotropy, localization, scaling parameters, etc.), hence the calculations are very slow. That is why we have the extreme need of more powerful computational resources. The GRID-version of the proposed application MultiscaleIVideoP would have a very wide range of potential users, because modern laboratories has commercial microscopes with digital output connection to PC and perform everyday tasks of complex static and dynamic morphology analysis: in biology, geology, chemistry, physics, materials science, etc.

Deploying this application on a Grid computing infrastructure, utilising hundreds of machines at the same time, allows harnessing sufficient computational power to undertake the simulations on a larger scale and in a much shorter timeframe. Running the simulations and analysing the results on the Grid provides the excessive computational power required.

<img width="570px" src="images/matlab1.jpg" />
</div>


<hr />

<a name="MATLAB2" />

  <p  class="titre_app">Kinetics of Defect Aggregation in Materials Science Simulated in Desktop Grid Computing Environment Installed in Ordinary Material Science Lab.</p>
  <p class="author">Y. Gordienko, Institut de Physique du Metal - Kiev - Ukraine</p>

<div class="abstract">

Aggregation processes are investigated in many branches of science: defect aggregation in materials science, population dynamics in biology, city growth and evolution in sociology. The typical simulation of crystal defect aggregation by our application SLinCA (Scaling Laws in Cluster Aggregation) takes several days and weeks on a single modern CPU, depending on the number of Monte Carlo steps (MCS). However, thousands of scenarios have to be simulated with different initial configurations to get statistically reliable results. Porting to distributed computing infrastructure (DCI) and parallel execution can reduce waiting time and scale up simulated systems to the desirable realistic values.Deploying this application on a Grid computing infrastructure, utilising hundreds of machines at the same time, allows harnessing sufficient computational power to undertake the simulations on a larger scale and in a much shorter timeframe. Running the simulations and analysing the results on the Grid provides the excessive computational power required.

<img width="570px" src="images/matlab2.png" />
</div>

</div>

<?php
include "basdepage.html";
?>
