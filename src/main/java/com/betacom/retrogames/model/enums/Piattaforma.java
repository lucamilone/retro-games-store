package com.betacom.retrogames.model.enums;

public enum Piattaforma 
{
	// Console Nintendo
	NES("Nintendo", 1983, "NES"),
	SNES("Nintendo", 1990, "SNES"),
	N64("Nintendo", 1996, "N64"),
	GAMECUBE("Nintendo", 2001, "GAMECUBE"),
	
	// Console Sony
	PS1("Sony", 1994, "PS1"),
	PS2("Sony", 2000, "PS2"),
	
	// Console Microsoft
	XBOX("Microsoft", 2001, "XBOX"),
	
	// Console Sega
	MEGA_DRIVE("Sega", 1988, "MEGA DRIVE"),
	SATURN("Sega", 1994, "SATURN"),
	DREAMCAST("Sega", 1998, "DREAMCAST"),
	
	// Console Portatili
	GB("Nintendo", 1989, "GB"),
	GBC("Nintendo", 1998, "GBC"),
	GBA("Nintendo", 2001, "GBA"),
	NDS("Nintendo", 2004, "NDS"),
	PSP("Sony", 2004, "PSP"),
	
	// Altre console
	ATARI_2600("Atari", 1977, "ATARI_2600"),
	NEO_GEO("SNK", 1990, "NEO_GEO");
	
	private final String produttore;
	private final int annoRilascio;
	private final String descrizione;
	
	private Piattaforma(String produttore, int annoRilascio, String descrizione) 
	{
		this.produttore = produttore;
		this.annoRilascio = annoRilascio;
		this.descrizione = descrizione;
	}
	
	public String getProduttore()
	{
		return produttore;
	}
	
	public int getAnnoRilascio()
	{
		return annoRilascio;
	}
	
	public String getDescrizione() 
	{ 
		return descrizione; 
	}
}