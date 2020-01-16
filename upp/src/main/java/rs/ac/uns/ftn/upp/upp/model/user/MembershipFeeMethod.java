package rs.ac.uns.ftn.upp.upp.model.user;

import java.io.Serializable;

/**
 * 
 * Nacin naplate clanarine casopisa. Da li se naplacuje citaocima ili autorima.
 * Ako se naplacuje citaocima -> READERS
 * Ako se naplacuje autorima -> AUTHORS 
 * 
 */
public enum MembershipFeeMethod implements Serializable {
	READERS, AUTHORS	
}
