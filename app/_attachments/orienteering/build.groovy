
import java.io.File
import net.sf.json.groovy.JsonSlurper

import org.antlr.stringtemplate.*
import org.antlr.stringtemplate.language.*

import com.google.common.io.Files 

@Grapes([
	@Grab(group='org.antlr', module='stringtemplate', version='3.2.1'),
	@Grab(group='net.sf.json-lib', module='json-lib', version='2.3', classifier='jdk15'),
	@Grab(group='com.google.guava', module='guava', version='11.0.2')
])
def f = new File("app.js")
def slurper = new JsonSlurper()
def result = slurper.parseText(f.getText())

def assetsDir = new File("assets")
def targetDir = "target"

def copyAssets(assetDir, outDir) {
	assetDir.listFiles().each { asset ->
		if (asset.isDirectory()) {
			def dir = new File(outDir, asset.name);
			dir.mkdir()
			copyAssets(asset, dir)
		} else {
			def out = new File(outDir, asset.name)
			println "... copy file from ${asset.absolutePath} to ${out.absolutePath}"
			Files.copy(asset, out)
		}
	}
}

this.args.each { locale -> 
	def outDir = new File("$targetDir/$locale")
	outDir.mkdirs()
	
	println "generating locale $locale to ${outDir.getAbsolutePath()}"
	
	println "... copying assets"
	copyAssets(assetsDir, outDir)
	
	def localeFile = new File("locale.${locale}.properties")
	if (!localeFile.exists()) {
		println "skipping $locale"
		return;
	}
	def messages = new Properties()
	localeFile.withInputStream { stream -> 
		messages.load(stream) 
	}

	def pages = result.pages
	pages.pagenames.each { pageName ->		
		println "about to process page named $pageName"
		def out = new File(outDir, "${pageName}.html")
		
		def page = pages[pageName]
		if (page["page-type"] == "list") {
			println "... its a list page"
			def entries = []
			page.content.each { entry -> 
				def linkedPage = pages[entry]
				def label = messages[linkedPage.title]
				println "... entry $label"
				entries << new ListEntry("label" : label, "link" : "${entry}.html")
			}
			def template = new StringTemplate(new File("templates/list-page.st").getText(), DefaultTemplateLexer.class)
			template.setAttribute("entries", entries)
			template.setAttribute("back", page.back)
			out.setText(template.toString())
		
		} else if (page["page-type"] == "controldescription") {
			def template = new StringTemplate(new File("templates/introduction-page.st").getText(), DefaultTemplateLexer.class)
			template.setAttribute("back", page.back)
			template.setAttribute("columna", messages["column.a.desc"])
			template.setAttribute("columnb", messages["column.b.desc"])
			template.setAttribute("columnc", messages["column.c.desc"])
			template.setAttribute("columnd", messages["column.d.desc"])
			template.setAttribute("columne", messages["column.e.desc"])
			template.setAttribute("columnf", messages["column.f.desc"])
			template.setAttribute("columng", messages["column.g.desc"])
			template.setAttribute("columnh", messages["column.h.desc"])
			out.setText(template.toString())
		
		} else if (page["page-type"] == "features") {
			println "... its a feature page"
			def input = result[page.data]
			if (page.data instanceof net.sf.json.JSONArray) {
				input = null
				page.data.each { el ->
					input = input == null ? result[page.data[0]] : input[el];
				}
			}
			def features = []
			input.features.each { feature ->
				feature.name = messages["feature.${feature.ref}.name"]
				feature.desc = messages["feature.${feature.ref}.desc"]
				features << feature
			}
			def template = new StringTemplate(new File("templates/feature-page.st").getText(), DefaultTemplateLexer.class);
			template.setAttribute("features", features)
			template.setAttribute("title", messages["page.${pageName}.title"])
			template.setAttribute("back", "${page.back}.html")
			template.setAttribute("pagescript", "bind.js")
			out.setText(template.toString())
			
		} else if (page["page-type"] == "random-features") {
			println "... its a random features page"
			def features = []
			page.data.each { dataRef ->
				println "... $dataRef"
				def data = result[dataRef]
				data.features.each { feature ->
					if (feature instanceof net.sf.json.JSONArray) {
						feature.each { fx -> 
							fx.name = messages["feature.${fx.ref}.name"]
							fx.desc = messages["feature.${fx.ref}.desc"]
							features << fx
						}
					} else {
						feature.name = messages["feature.${feature.ref}.name"]
						feature.desc = messages["feature.${feature.ref}.desc"]
						features << feature
					}
				}
			}
			def template = new StringTemplate(new File("templates/random-feature-page.st").getText(), DefaultTemplateLexer.class);
			template.setAttribute("features", features)
			template.setAttribute("title", messages["page.${pageName}.title"])
			template.setAttribute("back", "${page.back}.html")
			out.setText(template.toString())
		}
	}
}

class ListEntry {
	def label
	def link
}
