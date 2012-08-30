package com.github.rasifix.lazycouch;

public class DocumentRevision {
	
	private final String id;
	private final String revision;

	public DocumentRevision(String id, String revision) {
		this.id = id;
		this.revision = revision;
	}
	
	public String getId() {
		return id;
	}
	
	public String getRevision() {
		return revision;
	}
	
}
