
loader.register('trainings/~templates/activities', function(require) {

return Ember.Handlebars.compile("<div class=\"container\">\n  <table border=\"0\" style=\"width:100%; background-image:-webkit-linear-gradient(top, #08C, #04C); color:white; height:35px; margin-bottom:10px\">\n    <tr>\n      <td width=\"15px\">\n        <a {{action previous}} style=\"cursor:pointer\"><i class=\"icon-chevron-left icon-white\"></i></a>\n      </td>\n      <td width=\"980px\" align=\"center\">\n\t      <span style=\"font-weight: bold; font-size: 15px\">{{monthAndYear}}</span> ({{length}} activities)\n      </td>\n      <td width=\"15px\">\n\t      <a {{action next}} style=\"cursor:pointer\"><i class=\"icon-chevron-right icon-white\"></i></a>\n      </td>\n    </tr>\n  </table>\n  {{outlet}}\n</div>\n");

});

loader.register('trainings/~templates/activity-list', function(require) {

return Ember.Handlebars.compile("{{#if controller.content.loading}}\n  <div style=\"background-image:url('img/loader.gif'); width:32px; height:32px; margin-left:auto; margin-right:auto\">&nbsp;</div>\n{{else}}\n\t<table border=\"0\" class=\"table table-hover\" style=\"width:100%\">\n\t\t{{#each content}}\n\t\t<tr {{action showActivity this target=\"Trainings.router\"}}>\n\t\t\t<td style=\"padding-top:0px; padding-bottom:0px\"><img {{bindAttr src=\"sportUrl\"}} {{bindAttr title=\"sport\"}} width=\"32\" height=\"32\"/></td>\n\t\t\t<td>{{date}}</td>\n\t\t\t<td>{{duration}}</td>\n\t\t\t<td>{{distance}}</td>\n\t\t\t<td>{{speed}}</td>\n\t\t\t<td>{{#if avgHr}}{{avgHr}}{{/if}}</td>\n\t\t</tr>\n\t\t{{/each}}\n\t</table>\n{{/if}}\n");

});

loader.register('trainings/~templates/activity', function(require) {

return Ember.Handlebars.compile("<div class=\"container\">\n  <div class=\"row\">\n    <div class=\"span12\">\n  \t  <b>Date:</b> {{date}} | \n  \t  <b>Sport:</b> {{sport}} | \n  \t  <b>Distance:</b> {{distance}} | \n  \t  <b>Time:</b> {{duration}} \n  \t  {{#if hr}} | <b>HR:</b> {{hr.avg}} ({{hr.max}}){{/if}}\n    </div>\n  </div>\n  <div class=\"row\">\n  \t<div class=\"span10\">\n\t  \t<div id=\"map\"></div>\n  \t</div>\n  </div>\n  <div class=\"row\">\n  \t<div class=\"span10\">\n  \t  <div>\n  \t    <ul style=\"list-style-type:none; margin:0; padding:0;\">\n  \t      {{#if view.selectedTrackpoint}}\n  \t      {{#each view.selectedTrackpoint}}\n  \t      <li style=\"display:inline\">{{name}}: {{value}}</li>\n  \t      {{/each}}\n  \t      {{else}}\n  \t      &nbsp;\n  \t      {{/if}}\n  \t    </ul>\n  \t  </div>\n      <div id=\"graph\"></div>\n  \t</div>\n\t</div>\n</div>\n");

});

loader.register('trainings/~templates/application', function(require) {

return Ember.Handlebars.compile("<nav role=\"navigation\" class=\"navbar navbar-fixed-top navbar-inverse\">\n  <div class=\"navbar-inner\">\n    <div class=\"container\">\n      <a class=\"brand\" href=\"#\">Trainings</a>\n      {{view Trainings.NavView controllerBinding='Trainings.router.navController'}}\n    </div>\n  </div>\n</nav>\n<div id=\"main\" role=\"main\" class=\"container\">\n  {{outlet}}\n</div>\n");

});

loader.register('trainings/~templates/dashboard', function(require) {

return Ember.Handlebars.compile("<div class=\"container\">\n  <div class=\"row\">\n    <ul class=\"labels\">\n      <li>&nbsp;</li>\n      {{#each view.labels}}\n      <li>{{label}}: {{duration}}</li>\n      {{/each}}\n    </ul>\n    <div class=\"span12\">\n      <div id=\"dashboard\"></div>\n    </div>\n  </div>\n\n  <div class=\"row\">\n    <div class=\"span12\">\n      {{outlet activities}}\n    </div>\n  </div>\n  \n  <div class=\"row\">\n    <div class=\"span6\">\n      {{outlet sportsummary}}\n    </div>\n  </div>\n</div>\n");

});

loader.register('trainings/~templates/equipments', function(require) {

return Ember.Handlebars.compile("<div class=\"container\">\n\t<table class=\"table table-striped table-hover\">\n\t\t<tr>\n\t\t\t<th>Name</th>\n\t\t\t<th>Brand</th>\n\t\t\t<th>Date of Purchase</th>\n\t\t\t<th>Distance</th>\n\t\t</tr>\n\t\t{{#each controller}}\n\t\t<tr>\n\t\t\t<td>{{name}}</td>\n\t\t\t<td>{{brand}}</td>\n\t\t\t<td>{{dateOfPurchase}}</td>\n\t\t\t<td>{{distance}}</td>\n\t\t</tr>\n\t\t{{/each}}\n\t</table>\n</div>\n");

});

loader.register('trainings/~templates/nav', function(require) {

return Ember.Handlebars.compile("<ul class=\"nav\">\n  {{#view view.NavItemView item=\"dashboard\"}}\n    <a {{action gotoDashboard href=\"true\"}}>Dashboard</a>\n  {{/view}}\n  {{#view view.NavItemView item=\"activities\"}}\n    <a {{action gotoActivities href=\"true\"}}>Activities</a>\n  {{/view}}\n  {{#view view.NavItemView item=\"equipments\"}}\n    <a {{action gotoEquipments href=\"true\"}}>Equipments</a>\n  {{/view}}        \n</ul>");

});

loader.register('trainings/~templates/sport-summary', function(require) {

return Ember.Handlebars.compile("{{#if controller.content.loading}}\n  <div style=\"background-image:url('img/loader.gif'); width:32px; height:32px; margin-left:auto; margin-right:auto\">&nbsp;</div>\n{{else}}\n  <ul class=\"nav nav-pills\">\n    <li><a href=\"#\">2008</a></li>\n    <li><a href=\"#\">2009</a></li>\n    <li><a href=\"#\">2010</a></li>\n    <li><a href=\"#\">2011</a></li>\n    <li class=\"active\"><a href=\"#\">2012</a></li>\n  </ul>\n\t<table border=\"0\" class=\"table table-hover\" style=\"width:100%\">\n\t  <thead>\n\t    <th>Sport</th>\n\t    <th>Time</th>\n\t    <th>Distance</th>\n\t    <th>Avg. Speed</th>\n\t    <th>#</th>\n\t  </thead>\n\t\t{{#each content}}\n\t\t<tr {{action showActivity this target=\"Trainings.router\"}}>\n\t\t\t<td style=\"padding-top:0px; padding-bottom:0px\"><img {{bindAttr src=\"sportUrl\"}} {{bindAttr title=\"sport\"}} width=\"32\" height=\"32\"/></td>\n\t\t\t<td>{{duration}}</td>\n\t\t\t<td>{{distance}}</td>\n\t\t\t<td>{{speed}}</td>\n\t\t\t<td>{{count}}</td>\n\t\t</tr>\n\t\t{{/each}}\n\t</table>\n{{/if}}\n");

});
