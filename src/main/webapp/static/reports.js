function getReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	console.log(baseUrl);
	return baseUrl + "/api/report";
}

function FetchLiquidationReport(event){

	$("#noos-table").hide();
    $("#good-size-table").hide();
	var url = getReportUrl()+"/liquidation";
    successFx = function(data){
        displayLiquidationReportData(data);
    }
    ajaxRequest(url,'GET',1,successFx);


	return false;
}
function displayLiquidationReportData(data){

	var $tbody = $('#liquidation-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		console.log(data[i]);
		console.log(i);
		var row = '<tr>'
		+ '<td>'  + e.category + '</td>'
		+ '<td>'  + e.subCategory + '</td>'
		+ '<td>' + e.avgDiscount + '</td>'
		+ '<td>' + e.avgDiscountAfterCleanup + '</td>'
		+ '<td>' + e.revenueCleanup + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
	$("#liquidation-table").show();
}

function FetchNoosReport(event){

	$("#liquidation-table").hide();
    $("#good-size-table").hide();
	var url = getReportUrl()+"/noos";
    successFx = function(data){
        displayNoosReportData(data);
    }
    ajaxRequest(url,'GET',1,successFx);


	return false;
}
function displayNoosReportData(data){

	var $tbody = $('#noos-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		console.log(data[i]);
		console.log(i);
		var row = '<tr>'
		+ '<td>'  + e.category + '</td>'
		+ '<td>'  + e.styleCode + '</td>'
		+ '<td>' + e.styleRos + '</td>'
		+ '<td>' + e.styleRevContri + '</td>'

		+ '</tr>';
        $tbody.append(row);
	}
	$("#noos-table").show();
}

function FetchGoodSizeReport(event){

	$("#liquidation-table").hide();
    $("#noos-table").hide();
	var url = getReportUrl()+"/sizes";
    successFx = function(data){
        displayGoodSizesReportData(data);
    }
    ajaxRequest(url,'GET',1,successFx);


	return false;
}
function displayGoodSizesReportData(data){

	var $tbody = $('#good-size-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		console.log(data[i]);
		console.log(i);
		var row = '<tr>'
		+ '<td>'  + e.category + '</td>'
		+ '<td>'  + e.subCategory + '</td>'
		+ '<td>' + e.size + '</td>'
		+ '<td>' + e.revContri + '</td>'
		+ '<td>' + e.typeOfSizes + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
	$("#good-size-table").show();
}

function HideTable(){
    $("#liquidation-table").hide();
    $("#noos-table").hide();
    $("#good-size-table").hide();
}

function init(){
    $('#liquidation-report').click(FetchLiquidationReport);
    $('#noos-report').click(FetchNoosReport);
    $('#good-size-report').click(FetchGoodSizeReport);
}

$(document).ready(init);
$(document).ready(HideTable);