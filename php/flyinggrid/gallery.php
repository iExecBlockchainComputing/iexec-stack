<?php
include "entete.html";
?>


<div class="droite">

  <h2>Gallery</h2>

  <div id="textelettre" >
    <p>
      This page presents bootable images that can be used to launch virtual machines over <a href="http://www.flyinggrid.org">Flying Grid</a> deployments.
    </p>
    <p>
      It is recommended to first read the <a href="http://www.xtremweb-hep.org">&quot;XtremWeb-HEP extended introduction&quot;</a> document.
      This document detail usage as well as security considerations.
    </p>
  </div>

  <div style="margin-top:50px;padding-top:15px;padding-left:40px;border-style:groove">
    <h2>Bootable images</h2>
    <div style="padding-top:5px;padding-left:30px;">    
      <p><a href=""><img src="images/scientific_linux_logo-small.png" style="vertical-align:middle" alt="Scientific Linux"/> Scientific Linux 5.5 </a></p>
      <ul class="capable">
        <li class="enable"> contextualization : yes
        <li class="enable"> SSH connection : yes
        <li class="disable"> connect as root : no
        <li class="disable"> sudo usage : no
        <li class="disable"> access to LAN :  no
        <li> size : 
        <li> md5sum : 
      </ul>
      <p><a href=""><img src="images/ubuntu-logo-small.png" style="vertical-align:middle" alt="Ubuntu"/> Ubuntu 11.10</a></p>
      <ul class="capable">
        <li class="enable"> contextualization : yes
        <li class="enable"> SSH connection : yes
        <li class="disable"> connect as root : no
        <li class="disable"> sudo usage : no
        <li class="disable"> access to LAN :  no
        <li> size : 
        <li> md5sum : 
      </ul>
      <p><a href=""><img src="images/cernvm.png" height="50px" style="vertical-align:middle" alt="CernVM"/> CernVM 2.4</a> set up for <a href="http://atlas.web.cern.ch/Atlas/Collaboration/">Atlas</a> experiment</p>
      <ul class="capable">
        <li class="enable"> contextualization : yes
        <li class="enable"> SSH connection : yes
        <li class="disable"> connect as root : no
        <li class="disable"> sudo usage : no
        <li class="enable"> access to LAN :  yes
        <li> size : 
        <li> md5sum : 
      </ul>
    </div>
  </div>
</div>


</div> <!-- droite -->

<?php
include "basdepage.html";
?>
