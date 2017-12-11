<TABLE BORDER="0" CELLSPACING=5 WIDTH="100%">
<TR>
  <TD CLASS=title COLSPAN="2">
  <FONT SIZE="+1">Overview of {cluster}</FONT>
  </TD>
</TR>

<TR>
<TD ALIGN=left VALIGN=top>
<table cellspacing=1 cellpadding=1 width=100% border=0>
 <tr><td>CPUs Total:</td><td align=left><B>{cpu_num}</B></td></tr>
 <tr><td width=60%>Hosts up:</td><td align=left><B>{num_nodes}</B></td></tr>
 <tr><td>Hosts down:</td><td align=left><B>{num_dead_nodes}</B></td></tr>
 <td><td>&nbsp;</td></tr>
 <tr><td colspan=2>Localtime:<br>&nbsp;&nbsp;<b>{localtime}</b></td></tr>
 </table>
<!-- INCLUDE BLOCK : extra -->
 <hr>
</TD>

<TD ROWSPAN=2 ALIGN="CENTER" VALIGN=top>
<table cellspacing=1 cellpadding=1 width=85% border=0>
<tr>
<td align='center' bgcolor='CAFF7A'>
Hosts</td>
<TD ALIGN="center" VALIGN=top bgcolor="66FFFF">
Jobs</td>
</tr>
<tr>
<td align='right' bgcolor='CAFF7A'>
<IMG HEIGHT="147" WIDTH="395" ALT="{cluster} Alive hosts" 
   SRC="./graph.php?r={range}&m=ALIVE&vl=Alive hosts">
</td>
<TD  ALIGN="left" VALIGN=top bgcolor="66FFFF">
<IMG HEIGHT="147" WIDTH="395" ALT="{cluster} Completed jobs" 
   SRC="./graph.php?m=COMPLETEDS&r={range}&vl=Completed jobs">
</TD>
</tr><tr>
<td align="right" bgcolor="CAFF7A">
<IMG HEIGHT="147" WIDTH="395" ALT="{cluster} Active hosts" 
   SRC="./graph.php?m=ACTIVE&r={range}&vl=Active hosts">
</td>
<TD ALIGN="left" VALIGN="top" bgcolor="66FFFF">
<IMG HEIGHT="147" WIDTH="395" ALT="{cluster} Running jobs" 
   SRC="./graph.php?m=RUNNINGS&r={range}&vl=Running jobs">
</TD>
</tr>
<tr>
<td align="right" bgcolor="CAFF7A">
<IMG HEIGHT="147" WIDTH="395" ALT="{cluster} Available hosts" 
   SRC="./graph.php?m=AVAILABLE&r={range}&vl=Available hosts">
</td>
<TD ALIGN="left" VALIGN=top bgcolor="66FFFF">
<IMG HEIGHT="147" WIDTH="395" ALT="{cluster} Erroneus jobs" 
   SRC="./graph.php?m=ERRORS&r={range}&vl=Error jobs">
</TD>
</TR>
<tr>
<td align="right" bgcolor="CAFF7A">
</td>
<TD ALIGN="left" VALIGN=top bgcolor="66FFFF">
<IMG HEIGHT="147" WIDTH="395" ALT="{cluster} Pilot jobs" 
   SRC="./graph.php?m=PILOTJOB&r={range}&vl=Pilot jobs">
</TD>
</TR>
</table>
</TD>
</tr>

<TR>
 <TD align=center valign=top>

<!--
      $pie_args = "title=" . rawurlencode("Cluster Load Percentages");
      $pie_args .= "&size=250x150";
      foreach($load_colors as $name=>$color)
         {
            if (!array_key_exists($color, $percent_hosts))
               continue;
            $n = $percent_hosts[$color];
            $name_url = rawurlencode($name);
            $pie_args .= "&$name_url=$n,$color";
         }
   }
else
   {
      # Show pie chart of hosts up/down
      $pie_args = "title=" . rawurlencode("Host Status");
      $pie_args .= "&size=250x150";
      $up_color = $load_colors["50-75"];
      $down_color = $load_colors["down"];
      $pie_args .= "&Up=$cluster[HOSTS_UP],$up_color";
      $pie_args .= "&Down=$cluster[HOSTS_DOWN],$down_color";
   }
-->
  <IMG SRC="./pie.php?{pie_args}" ALT="Pie Chart" BORDER="0">
<!--
  <IMG SRC="./pie.php?title=Cluster%20Load%20Percentages&size=250x150&RUNNINGS=15,ececec" ALT="Pie Chart" BORDER="0">
-->
 </TD>
</TR>
</TABLE>


<TABLE BORDER="0" WIDTH="100%">
<TR>
  <TD CLASS=title COLSPAN="2"> 
  <FONT SIZE="-1">
  Show Hosts:
  yes<INPUT type=radio name="sh" value="1" OnClick="ganglia_form.submit();" {checked1}>
  no<INPUT type=radio name="sh" value="0" OnClick="ganglia_form.submit();" {checked0}>
  </FONT>
  |
  {cluster} <strong>{metric}</strong>
  last <strong>{range}</strong>
  sorted <strong>{sort}</strong>
  |
   <FONT SIZE="-1">
   Columns&nbsp;&nbsp;{cols_menu}
   </FONT>
  </TD>
</TR>
</TABLE>

<CENTER>
<TABLE>
<TR>
<!-- START BLOCK : sorted_list -->
{metric_image}{br}
<!-- END BLOCK : sorted_list -->
</TR>
</TABLE>

<p>
(Nodes colored by 1-minute load) | <A HREF="./node_legend.html" ALT="Node Image egend">Legend</A>

</CENTER>
