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
		<div data-role="content" style="text-align:center">
			<table border="0" cellspacing="0" cellpadding="0" style="width:250px;margin-left:auto;margin-right:auto">
				<tr>
					<td><img src="features/controldescription.svg" width="250"/></td>
				</tr>
				<tr>
					<td style="text-align:left">
						<b>A</b>: $columna$<br />
						<b>B</b>: $columnb$<br />
						<b>C</b>: $columnc$<br />
						<b>D</b>: $columnd$<br />
						<b>E</b>: $columne$<br />
						<b>F</b>: $columnf$<br />
						<b>G</b>: $columng$<br />
						<b>H</b>: $columnh$<br />
					</td>
				</tr>
			</table>
		</div>
	</div>

	</body>
</html>
