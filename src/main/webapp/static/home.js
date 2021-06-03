function getStyleUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	console.log(baseUrl);
	return baseUrl + "/api/style";
}

function getStoreUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	console.log(baseUrl);
	return baseUrl + "/api/store";
}

function getSkuUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	console.log(baseUrl);
	return baseUrl + "/api/sku";
}

function getSalesUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	console.log(baseUrl);
	return baseUrl + "/api/sales";
}


function resetUploadDialog(){
	//Reset file name
	var $file = $('#style-file');
	$file.val('');
	$('#style-file-name').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];

}

function displayUploadStyle(){

    resetUploadDialog();
    $('#upload-style-modal').modal('toggle');

}

function displayUploadStore(){
//    resetUploadDialog();
    $('#upload-store-modal').modal('toggle');
}

function displayUploadSku(){
    $('#upload-sku-modal').modal('toggle');
}

function displayUploadSales(){
    $('#upload-sales-modal').modal('toggle');
}

function updateStyleFileName(){
    var $file = $('#style-file');
    var fileName = $file.val();
    fileName = fileName.substr(12);
    $('#style-file-name').html(fileName);
}

function updateStoreFileName(){
    var $file = $('#store-file');
    var fileName = $file.val();
    fileName = fileName.substr(12);
    $('#store-file-name').html(fileName);
}

function updateSkuFileName(){
    var $file = $('#sku-file');
    var fileName = $file.val();
    fileName = fileName.substr(12);
    $('#sku-file-name').html(fileName);
}

function updateSalesFileName(){
    var $file = $('#sales-file');
    var fileName = $file.val();
    fileName = fileName.substr(12);
    $('#sales-file-name').html(fileName);
}

//function uploadStyleFile(){
//    var $file = $('#style-file');
//    var file2 = $('#style-file')[0].files[0];
//    console.log(file2);
//    formData = new FormData();
//    formData.append("file",file2);
//    url = getStyleUrl();
//    ajaxFileUploadRequest(url,'POST',formData);
//    	for(var pair of formData.entries()) {
//           console.log(pair[0]+ ', '+ pair[1]);
//        }

    //    console.log(formData.values());
    //	console.log(url);
    //	$.ajax({
    //            url: url,
    //            type: 'GET',
    //            headers: {
    //               'Content-Type': 'application/json'
//            }
//    });
//   	$.ajax({
//       	url: url,
//       	type: 'POST',
//        data: formData,
//      	contentType: false,
//        processData: false,
//        error: handleAjaxError
//    });
//}

function uploadFile($file, url){
    var file2 = $file[0].files[0];
    console.log(file2);
    formData = new FormData();
    formData.append("file",file2);
//    url = getStoreUrl();
    ajaxFileUploadRequest(url,'POST',formData);
}

const fileSuccessFx = function(data,fileName){

      let blob = new Blob([data], { type: 'text;charset=utf-8;' });
                // IE 10+
      if(navigator.msSaveBlob) {
          navigator.msSaveBlob(blob, fileName);
          return;
      }
      let link = document.createElement("a");
      // Browsers that support HTML5 download attribute
      let url = URL.createObjectURL(blob);
      link.setAttribute("href", url);
      link.setAttribute("download", fileName);
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url)
}

function downloadFiles(url,fileName){
    ajaxRequest(url,'GET',1,fileSuccessFx,fileName);
    console.log("request made");
}

function downloadErrorFiles(url,fileName){
    url+="/errors";
    console.log(url);
    ajaxRequest(url,'GET',1,fileSuccessFx,fileName);
    console.log("request made");
}

function init(){
    $('#upload-style').click(displayUploadStyle);
    $('#upload-store').click(displayUploadStore);
    $('#upload-sku').click(displayUploadSku);
    $('#upload-sales').click(displayUploadSales);

    $('#style-file').on('change', updateStyleFileName);
    $('#store-file').on('change', updateStoreFileName);
    $('#sku-file').on('change',updateSkuFileName);
    $('#sales-file').on('change', updateSalesFileName);

    $('#process-data-style').click(() => uploadFile($('#style-file'),getStyleUrl()));
    $('#process-data-store').click(() => uploadFile($('#store-file'),getStoreUrl()));
    $('#process-data-sales').click(() => uploadFile($('#sales-file'),getSalesUrl()));
    $('#process-data-sku').click(() => uploadFile($('#sku-file'),getSkuUrl()));

    $('#download-style').click(() => downloadFiles(getStyleUrl(),"Style"));
    $('#download-store').click(() => downloadFiles(getStoreUrl(),"Store"));
    $('#download-sku').click(() => downloadFiles(getSkuUrl(),"Sku"));
    $('#download-sales').click(() => downloadFiles(getSalesUrl(),"Sales"));

    $('#download-errors-style').click(() => downloadFiles(getStyleUrl()+"/errors","Style_Errors"));
    $('#download-errors-store').click(() => downloadFiles(getStoreUrl()+"/errors","Store_Errors"));
    $('#download-errors-sku').click(() => downloadFiles(getSkuUrl()+"/errors","Sku_Errors"));
    $('#download-errors-sales').click(() => downloadFiles(getSalesUrl()+"/errors","Sales_Errors"));

}

$(document).ready(init);