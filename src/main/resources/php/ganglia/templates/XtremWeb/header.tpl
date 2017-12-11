<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
<HEAD>
<TITLE>Ganglia Cluster Toolkit:: {page_title}</TITLE>
<META http-equiv="Content-type" content="text/html; charset=utf-8">
<META http-equiv="refresh" content="{refresh}{redirect}" >
<LINK rel="stylesheet" href="./styles.css" type="text/css">
</HEAD>
<BODY BGCOLOR="#FFFFFF">

<table border="0" width="90%">
  <tr width="90%">
    <td align="right" colspan="2">
       <table border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="430" height="146" valign="top" align="left">
            <a href="http://www.lal.in2p3.fr">
              <img src="{images}/lal-coul-500.jpg" width="280" height="146" alt="logo LAL" border="0">
            </a>
          </td>
        </tr>
       </table>
     </td>
  </tr>
</table>


<FORM ACTION="{page}" METHOD="GET" NAME="ganglia_form">
<TABLE WIDTH="100%">
<TR>

  <TABLE WIDTH="100%" CELLPADDING="8" CELLSPACING="0" BORDER=0>
  <TR BGCOLOR="#DDDDDD">
     <TD BGCOLOR="#DDDDDD">
     <FONT SIZE="+1">
     <B>{page_title} for {date}</B>
     </FONT>
     </TD>
     <TD BGCOLOR="#DDDDDD" ALIGN="RIGHT">
     <INPUT TYPE="SUBMIT" VALUE="Get Fresh Data">
     </TD>
  <TD ROWSPAN="2" WIDTH="150">
  <A HREF="http://ganglia.sourceforge.net/">
  <IMG SRC="{images}/ganglia.jpg" HEIGHT="91" WIDTH="150" 
      ALT="Ganglia Cluster Toolkit" BORDER="0"></A>
  </TD>
  </TR>
  <TR>
     <TD COLSPAN="1">
     {metric_menu} &nbsp;&nbsp;
     {range_menu}&nbsp;&nbsp;
     {sort_menu}
     </TD>
     <TD colspan=2>
	  <B>{alt_view}</B>
     </TD>
  </TR>
  </TABLE>

  </TD>
</TR>
</TABLE> 

<FONT SIZE="+1">
{node_menu}
</FONT>
<HR SIZE="1" NOSHADE>
