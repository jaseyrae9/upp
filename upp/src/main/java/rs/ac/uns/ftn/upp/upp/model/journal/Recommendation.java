package rs.ac.uns.ftn.upp.upp.model.journal;

import java.io.Serializable;

/**
 * ACCEPTED 	- Prihvatiti
 * MINOR_EDITS 	- Prihvatiti uz manje ispravke 
 * MAJOR_EDITS 	- Uslovno prihvatiti uz vece ispravke
 * REJECT 		- Odbiti
 * 
 */
public enum Recommendation implements Serializable {
	ACCEPTED, MINOR_EDITS, MAJOR_EDITS, REJECT
}
