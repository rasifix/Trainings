<!DOCTYPE html> 
<html manifest="ioccd.appcache"> 
	<head> 
		<title>$title$</title> 
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
	    <meta name="apple-touch-fullscreen" content="yes">
	    <meta name="apple-mobile-web-app-capable" content="yes">
	    <meta name="apple-mobile-web-app-status-bar-style" content="black">
	
		<link rel="stylesheet" href="css/jquery.mobile-1.1.0.min.css" />
		<link rel="stylesheet" href="css/ioccd.css" />
		
		<script src="js/jquery-1.7.1.min.js"></script>
		<script src="js/jquery.mobile-1.1.0.min.js"></script>
		<script src="js/jquery.bxSlider.js"></script>
		<script src="js/bind-random.js"></script>
	</head> 
	<body> 

		<div data-role="page">
			<div data-role="header">
				<a href="$back$" data-icon="back">Back</a>
				<h1>$title$</h1>
			</div>
			<div data-role="content" style="text-align:center">
				<div id="slider">
					$features:templates/testfeature()$
				</div>
			</div>
		</div>
		
	</body>
</html>