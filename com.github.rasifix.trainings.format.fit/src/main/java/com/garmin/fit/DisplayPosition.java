////////////////////////////////////////////////////////////////////////////////
// The following FIT Protocol software provided may be used with FIT protocol
// devices only and remains the copyrighted property of Dynastream Innovations Inc.
// The software is being provided on an "as-is" basis and as an accommodation,
// and therefore all warranties, representations, or guarantees of any kind
// (whether express, implied or statutory) including, without limitation,
// warranties of merchantability, non-infringement, or fitness for a particular
// purpose, are specifically disclaimed.
//
// Copyright 2008 Dynastream Innovations Inc.
////////////////////////////////////////////////////////////////////////////////
// ****WARNING****  This file is auto-generated!  Do NOT edit this file.
// Profile Version = 1.50Release
// Tag = $Name: AKW1_500 $
////////////////////////////////////////////////////////////////////////////////


package com.garmin.fit;

public enum DisplayPosition {
   DEGREE((short)0),
   DEGREE_MINUTE((short)1),
   DEGREE_MINUTE_SECOND((short)2),
   AUSTRIAN_GRID((short)3),
   BRITISH_GRID((short)4),
   DUTCH_GRID((short)5),
   HUNGARIAN_GRID((short)6),
   FINNISH_GRID((short)7),
   GERMAN_GRID((short)8),
   ICELANDIC_GRID((short)9),
   INDONESIAN_EQUATORIAL((short)10),
   INDONESIAN_IRIAN((short)11),
   INDONESIAN_SOUTHERN((short)12),
   INDIA_ZONE_0((short)13),
   INDIA_ZONE_IA((short)14),
   INDIA_ZONE_IB((short)15),
   INDIA_ZONE_IIA((short)16),
   INDIA_ZONE_IIB((short)17),
   INDIA_ZONE_IIIA((short)18),
   INDIA_ZONE_IIIB((short)19),
   INDIA_ZONE_IVA((short)20),
   INDIA_ZONE_IVB((short)21),
   IRISH_TRANSVERSE((short)22),
   IRISH_GRID((short)23),
   LORAN((short)24),
   MAIDENHEAD_GRID((short)25),
   MGRS_GRID((short)26),
   NEW_ZEALAND_GRID((short)27),
   NEW_ZEALAND_TRANSVERSE((short)28),
   QATAR_GRID((short)29),
   MODIFIED_SWEDISH_GRID((short)30),
   SWEDISH_GRID((short)31),
   SOUTH_AFRICAN_GRID((short)32),
   SWISS_GRID((short)33),
   TAIWAN_GRID((short)34),
   UNITED_STATES_GRID((short)35),
   UTM_UPS_GRID((short)36),
   WEST_MALAYAN((short)37),
   BORNEO_RSO((short)38),
   ESTONIAN_GRID((short)39),
   LATVIAN_GRID((short)40),
   SWEDISH_REF_99_GRID((short)41),
   INVALID((short)255);


   protected short value;




   private DisplayPosition(short value) {
     this.value = value;
   }

   protected static DisplayPosition getByValue(final Short value) {
      for (final DisplayPosition type : DisplayPosition.values()) {
         if (value == type.value)
            return type;
      }

      return DisplayPosition.INVALID;
   }

   public short getValue() {
      return value;
   }


}
