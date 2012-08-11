<!DOCTYPE html> 
<html manifest="ioccd.appcache"> 
	<head> 
		<title>Orienteering</title> 
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
	    <meta name="apple-touch-fullscreen" content="yes">
	    <meta name="apple-mobile-web-app-capable" content="yes">
	    <meta name="apple-mobile-web-app-status-bar-style" content="black">
		<link rel="stylesheet" href="css/jquery.mobile-1.1.0.min.css" />
		<style>
			.bx-wrapper {
				margin-left: auto;
				margin-right: auto;
			}
		</style>
		<script src="js/jquery-1.7.1.min.js"></script>
		<script src="js/jquery.mobile-1.1.0.min.js"></script>
		<script src="js/bind.js"></script>
	</head> 
	<body> 

	<div data-role="page">
		<div data-role="header">
			$if(back)$
			<a href="$back$.html" data-icon="back">Back</a>
			$endif$
			<h1>Control Descriptions</h1>
		</div>
		<div data-role="content">
			<ul data-role="listview" data-theme="g">
				$entries:templates/listentry()$
			</div>
		</div>
	</div>

	</body>
</html>