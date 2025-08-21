package com.betacom.retrogames.model.enums;

public enum Piattaforma 
{
	// Console Nintendo
	NES("Nintendo", 1983),
	SNES("Nintendo", 1990),
	N64("Nintendo", 1996),
	GAMECUBE("Nintendo", 2001),
	
	// Console Sony
	PS1("Sony", 1994),
	PS2("Sony", 2000),
	
	// Console Microsoft
	XBOX("Microsoft", 2001),
	
	// Console Sega
	MEGA_DRIVE("Sega", 1988),
	SATURN("Sega", 1994),
	DREAMCAST("Sega", 1998),
	
	// Console Portatili
	GB("Nintendo", 1989),
	GBC("Nintendo", 1998),
	GBA("Nintendo", 2001),
	NDS("Nintendo", 2004),
	PSP("Sony", 2004),
	
	// Altre console
	ATARI_2600("Atari", 1977),
	NEO_GEO("SNK", 1990);
	
	private final String produttore;
	private final int annoRilascio;
	
	private Piattaforma(String produttore, int annoRilascio) 
	{
		this.produttore = produttore;
		this.annoRilascio = annoRilascio;
	}
	
	public String getProduttore()
	{
		return produttore;
	}
	
	public int getAnnoRilascio()
	{
		return annoRilascio;
	}
}