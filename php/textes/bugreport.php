<?php
echo <<<EOF
<form method="post">
<input type="hidden" name="section" value="changes">
<input type="hidden" name="subsection" value="bugreport_action">
<blockquote>
<p class="TITRE">Bug report</p>
<hr WIDTH="100%"><br>
<blockquote>
<table border="0">
<tr><td>Name:</td><td><input type="text" name="name"></td></tr>
<tr><td>Email:</td><td><input type="text" name="email"></td></tr>
<tr><td>Category:</td><td><select name="category">
<option selected>General issue
<option>Server
<option>Workers
<option>Clients
<option>Installation problem
<option>Compilation problem
<option>Scripting problem
<option>Application problem
</select></td></tr>
<tr><td colspan="2"><textarea name="bugdescription" cols="50" rows="15"></textarea></td>
<tr><td><input type="submit" name="submit" value="Submit Bug"></td>
<td><input type="reset" name="reset" value="Reset"></td></tr>
</table>
</blockquote>
</form>
<blockquote>
EOF;


?>
