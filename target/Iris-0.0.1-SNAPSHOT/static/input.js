function getAlgoUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	console.log(baseUrl);
	return baseUrl + "/api/algo";
}

function addInputParameters(){
    url = getAlgoUrl();
    var $form = $("#report-form");
	var json = toJson($form);
	console.log(json);
    ajaxRequest(url,'POST',json);
}

function runModule(){
    url = getAlgoUrl();
    let successFx = function(){
        successMessageDisplay("Algo Run Successful.");
    }
    ajaxRequest(url,'GET',1,successFx);
}

function init(){
    $('#input-parameters').click(addInputParameters);
    $('#run-module').click(runModule);
}

$(document).ready(init);