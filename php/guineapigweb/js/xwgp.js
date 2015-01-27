// Jquery document ready event, is this really necessary ?
$('document').ready(function() {

	$("#save_simu").click(function(){
	  $.post(base_url + "index.php/xwgp/ajax_save_simu", { simu_id : $("input#simu_id").val(), name : $("#name").val(), number : $("#number").val(), configuration : $("#configuration").val() }, function(data) {
		  //alert("uuu");//alert(data);
		  $( "#editConfDialog" ).dialog('close');
		  get_list_simu();
	  }, "json");
	  return false;
	})

	$("#create_simu").click(function(){
	  $.post(base_url + "index.php/xwgp/ajax_create_simu", { name : $("#name").val(),number : $("#number").val(), configuration : $("#configuration").val() }, function(data) {
		  $( "#editConfDialog" ).dialog('close');
		  get_list_simu();
		  //alert("uuu");//alert(data);
	  }, "json");
	  return false;
	})

	get_list_simu();
	  
});


// Various functions

function get_list_simu() {
	$.post(base_url + 'index.php/xwgp/ajax_get_list_simu', {xwlogin : xwlogin}, function(data) {
	  if (data.status=='ok') {
		  $("div#list_simu").html(data.content);
	  } else {
		  $("div#list_simu").html('status not ok');
	  }
	}, "json");
  }

function get_simu(simu_id) {
  //alert($("a#edit_simu_config3").attr('href') +'-'+$("a#edit_simu_config3").text());
  $.post(base_url + 'index.php/xwgp/ajax_get_simu', {simu_id : simu_id}, function(data) {
	if (data.status=='ok') {
		$("input#simu_id").val(data.id);
		$("#name").val(data.name);
		$("#number").val(data.number);
		$("#configuration").val(data.configuration);
		//alert(simu_id+"*"+data.id+"*"+$("input#simu_id").val())
	} else {
		$("input#name").val('status not ok');
	}
  }, "json");
}

function click_delete_simu(simu_id)
{
	$.post(base_url + 'index.php/xwgp/ajax_delete_simu', {simu_id : simu_id}, function(data) {
	  if (data.status=='ok') {
		  $("div#list_simu").html(data.content);
	  } else {
		  $("div#list_simu").html('status not ok');
	  }
	}, "json");
	return false;
}

function click_run_simu(simu_id)
{
	$.post(base_url + 'index.php/xwgp/ajax_run_simu', {simu_id : simu_id}, function(data) { $("div#list_simu").html(data.content); }, "json");
	return false;
}

function empty_SimuDialog()
{
	$("input#simu_id").val('');
	$("#name").val('');
	$("#configuration").val('');
}

function click_edit_simu(simu_id)
{
	$( "#editConfDialog" ).dialog({ height: 400, width: 330, title: "Edit Simulation"  });
	empty_SimuDialog();
	$( "#create_simu" ).hide();
	$( "#save_simu" ).show();
	$( "#editConfDialog" ).show();
	get_simu(simu_id);
	return false;
}

function click_create_simu()
{
	$( "#editConfDialog" ).dialog({ height: 400, width: 330, title: "Create Simulation" });
	empty_SimuDialog();
	$( "#save_simu" ).hide();
	$( "#create_simu" ).show();
	$( "#editConfDialog" ).show();
	
	$("#number").val('2');
	$.post(base_url + 'index.php/xwgp/ajax_get_file_content', {file_location : "guineapig/acc.dat"}, function(data) {
	  if (data.status=='ok') {
		  $("#configuration").val(data.content);
		  //alert(simu_id+"*"+data.id+"*"+$("input#simu_id").val())
	  } else {
		  $("#configuration").val('status not ok');
	  }
	}, "json");
	
	return false;
}

function click_view_results(simu_id)
{
	viewresdiv= "#viewres"+simu_id;
	//test if div already exists before creating it
	if ($(viewresdiv).length <= 0) {
		div = $('<div id="viewres' + simu_id + '" title="View Results"></div>');
		$("body").prepend(div);
	}
	$(viewresdiv).html('');
	$(viewresdiv).dialog({ height: 400, width: 310 });
	$(viewresdiv).show();
	$.post(base_url + 'index.php/xwgp/ajax_get_results', {simu_id : simu_id}, function(data) {
	  if (data.status=='ok') {
		  $(viewresdiv).html(data.content);
		  //alert(simu_id+"*"+data.id+"*"+$("input#simu_id").val())
	  } else {
		  $(viewresdiv).html('status not ok');
	  }
	}, "json");
	return false;
}

function click_view_plot(jsonFileToPlot, plotdivname)
{
	plotdivc= "#"+plotdivname+"c";
	plotdiv= "#"+plotdivname;
	//test if div already exists before creating it
	if ($(plotdivc).length <= 0) {
		//alert('no');
		div = $('<div id="' + plotdivname + 'c" title="View Plot"></div>');
		$("body").prepend(div);
		div = $('<div id="' + plotdivname + '" style="width:90%;height:90%;"></div>');
		$("div"+plotdivc).prepend(div);
	}
	$(plotdiv).html('');
	//alert(jsonFileToPlot);
	$("div"+plotdivc).dialog({ height: 400, width: 550 });
	//$("div"+plotdiv).height(340);
	//$("div"+plotdiv).width(500);
	$(plotdivc).show();
	$(plotdiv).show();
	$.getJSON(base_url + jsonFileToPlot, function(json) {
		options = { series: { lines: { show: false }, points: { show: true, radius: 1, symbol: "circle", shadowSize:0} } };
		data2=[ {  data: json } ];
		plot = $.plot($(plotdiv), data2, options);
	});
	return false;
}

function click_view_img(imguri, plotdivname)
{
	//test if div already exists before creating it
	if ($(plotdivname).length <= 0) {
		div = $('<div id="' + plotdivname + '" title="View Image"></div>');
		//div = $("<div id=\"" + plotdivname + "\"></div>");
		$("body").prepend(div);
	}
	plotdiv= "#"+plotdivname;
	$(plotdiv).html('<img height="98%" width="98%" src="'+imguri+'">');
	$(plotdiv).dialog({ height: 400, width: 550 });
	$(plotdiv).show();
	return false;
}

