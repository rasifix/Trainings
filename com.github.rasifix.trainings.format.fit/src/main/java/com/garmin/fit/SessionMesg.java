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


public class SessionMesg extends Mesg implements MesgWithEvent {


   public SessionMesg() {
      super(Factory.createMesg(MesgNum.SESSION));
   }

   public SessionMesg(final Mesg mesg) {
      super(mesg);
   }


   /**
    * Get message_index field
    * Comment: Selected bit is set for the current session.
    *
    * @return message_index
    */
   public Integer getMessageIndex() {
      return getFieldIntegerValue(254, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set message_index field
    * Comment: Selected bit is set for the current session.
    *
    * @param messageIndex
    */
   public void setMessageIndex(Integer messageIndex) {
      setFieldValue(254, 0, messageIndex, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get timestamp field
    * Units: s
    * Comment: Sesson end time.
    *
    * @return timestamp
    */
   public DateTime getTimestamp() {
      return timestampToDateTime(getFieldLongValue(253, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD));
   }

   /**
    * Set timestamp field
    * Units: s
    * Comment: Sesson end time.
    *
    * @param timestamp
    */
   public void setTimestamp(DateTime timestamp) {
      setFieldValue(253, 0, timestamp.getTimestamp(), Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get event field
    * Comment: session
    *
    * @return event
    */
   public Event getEvent() {
      Short value = getFieldShortValue(0, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return Event.getByValue(value);
   }

   /**
    * Set event field
    * Comment: session
    *
    * @param event
    */
   public void setEvent(Event event) {
      setFieldValue(0, 0, event.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get event_type field
    * Comment: stop
    *
    * @return event_type
    */
   public EventType getEventType() {
      Short value = getFieldShortValue(1, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return EventType.getByValue(value);
   }

   /**
    * Set event_type field
    * Comment: stop
    *
    * @param eventType
    */
   public void setEventType(EventType eventType) {
      setFieldValue(1, 0, eventType.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get start_time field
    *
    * @return start_time
    */
   public DateTime getStartTime() {
      return timestampToDateTime(getFieldLongValue(2, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD));
   }

   /**
    * Set start_time field
    *
    * @param startTime
    */
   public void setStartTime(DateTime startTime) {
      setFieldValue(2, 0, startTime.getTimestamp(), Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get start_position_lat field
    * Units: semicircles
    *
    * @return start_position_lat
    */
   public Integer getStartPositionLat() {
      return getFieldIntegerValue(3, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set start_position_lat field
    * Units: semicircles
    *
    * @param startPositionLat
    */
   public void setStartPositionLat(Integer startPositionLat) {
      setFieldValue(3, 0, startPositionLat, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get start_position_long field
    * Units: semicircles
    *
    * @return start_position_long
    */
   public Integer getStartPositionLong() {
      return getFieldIntegerValue(4, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set start_position_long field
    * Units: semicircles
    *
    * @param startPositionLong
    */
   public void setStartPositionLong(Integer startPositionLong) {
      setFieldValue(4, 0, startPositionLong, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get sport field
    *
    * @return sport
    */
   public Sport getSport() {
      Short value = getFieldShortValue(5, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return Sport.getByValue(value);
   }

   /**
    * Set sport field
    *
    * @param sport
    */
   public void setSport(Sport sport) {
      setFieldValue(5, 0, sport.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get sub_sport field
    *
    * @return sub_sport
    */
   public SubSport getSubSport() {
      Short value = getFieldShortValue(6, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return SubSport.getByValue(value);
   }

   /**
    * Set sub_sport field
    *
    * @param subSport
    */
   public void setSubSport(SubSport subSport) {
      setFieldValue(6, 0, subSport.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get total_elapsed_time field
    * Units: s
    * Comment: Time (includes pauses)
    *
    * @return total_elapsed_time
    */
   public Float getTotalElapsedTime() {
      return getFieldFloatValue(7, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set total_elapsed_time field
    * Units: s
    * Comment: Time (includes pauses)
    *
    * @param totalElapsedTime
    */
   public void setTotalElapsedTime(Float totalElapsedTime) {
      setFieldValue(7, 0, totalElapsedTime, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get total_timer_time field
    * Units: s
    * Comment: Timer Time (excludes pauses)
    *
    * @return total_timer_time
    */
   public Float getTotalTimerTime() {
      return getFieldFloatValue(8, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set total_timer_time field
    * Units: s
    * Comment: Timer Time (excludes pauses)
    *
    * @param totalTimerTime
    */
   public void setTotalTimerTime(Float totalTimerTime) {
      setFieldValue(8, 0, totalTimerTime, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get total_distance field
    * Units: m
    *
    * @return total_distance
    */
   public Float getTotalDistance() {
      return getFieldFloatValue(9, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set total_distance field
    * Units: m
    *
    * @param totalDistance
    */
   public void setTotalDistance(Float totalDistance) {
      setFieldValue(9, 0, totalDistance, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get total_cycles field
    * Units: cycles
    *
    * @return total_cycles
    */
   public Long getTotalCycles() {
      return getFieldLongValue(10, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set total_cycles field
    * Units: cycles
    *
    * @param totalCycles
    */
   public void setTotalCycles(Long totalCycles) {
      setFieldValue(10, 0, totalCycles, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get total_strides field
    * Units: strides
    *
    * @return total_strides
    */
   public Long getTotalStrides() {
      return getFieldLongValue(10, 0, Profile.SubFields.SESSION_MESG_TOTAL_CYCLES_FIELD_TOTAL_STRIDES);
   }

   /**
    * Set total_strides field
    * Units: strides
    *
    * @param totalStrides
    */
   public void setTotalStrides(Long totalStrides) {
      setFieldValue(10, 0, totalStrides, Profile.SubFields.SESSION_MESG_TOTAL_CYCLES_FIELD_TOTAL_STRIDES);
   }

   /**
    * Get total_calories field
    * Units: kcal
    *
    * @return total_calories
    */
   public Integer getTotalCalories() {
      return getFieldIntegerValue(11, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set total_calories field
    * Units: kcal
    *
    * @param totalCalories
    */
   public void setTotalCalories(Integer totalCalories) {
      setFieldValue(11, 0, totalCalories, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get total_fat_calories field
    * Units: kcal
    *
    * @return total_fat_calories
    */
   public Integer getTotalFatCalories() {
      return getFieldIntegerValue(13, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set total_fat_calories field
    * Units: kcal
    *
    * @param totalFatCalories
    */
   public void setTotalFatCalories(Integer totalFatCalories) {
      setFieldValue(13, 0, totalFatCalories, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get avg_speed field
    * Units: m/s
    * Comment: total_distance / total_timer_time
    *
    * @return avg_speed
    */
   public Float getAvgSpeed() {
      return getFieldFloatValue(14, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set avg_speed field
    * Units: m/s
    * Comment: total_distance / total_timer_time
    *
    * @param avgSpeed
    */
   public void setAvgSpeed(Float avgSpeed) {
      setFieldValue(14, 0, avgSpeed, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get max_speed field
    * Units: m/s
    *
    * @return max_speed
    */
   public Float getMaxSpeed() {
      return getFieldFloatValue(15, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set max_speed field
    * Units: m/s
    *
    * @param maxSpeed
    */
   public void setMaxSpeed(Float maxSpeed) {
      setFieldValue(15, 0, maxSpeed, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get avg_heart_rate field
    * Units: bpm
    * Comment: average heart rate (excludes pause time)
    *
    * @return avg_heart_rate
    */
   public Short getAvgHeartRate() {
      return getFieldShortValue(16, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set avg_heart_rate field
    * Units: bpm
    * Comment: average heart rate (excludes pause time)
    *
    * @param avgHeartRate
    */
   public void setAvgHeartRate(Short avgHeartRate) {
      setFieldValue(16, 0, avgHeartRate, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get max_heart_rate field
    * Units: bpm
    *
    * @return max_heart_rate
    */
   public Short getMaxHeartRate() {
      return getFieldShortValue(17, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set max_heart_rate field
    * Units: bpm
    *
    * @param maxHeartRate
    */
   public void setMaxHeartRate(Short maxHeartRate) {
      setFieldValue(17, 0, maxHeartRate, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get avg_cadence field
    * Units: rpm
    * Comment: total_cycles / total_timer_time if non_zero_avg_cadence otherwise total_cycles / total_elapsed_time
    *
    * @return avg_cadence
    */
   public Short getAvgCadence() {
      return getFieldShortValue(18, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set avg_cadence field
    * Units: rpm
    * Comment: total_cycles / total_timer_time if non_zero_avg_cadence otherwise total_cycles / total_elapsed_time
    *
    * @param avgCadence
    */
   public void setAvgCadence(Short avgCadence) {
      setFieldValue(18, 0, avgCadence, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get avg_running_cadence field
    * Units: strides/min
    *
    * @return avg_running_cadence
    */
   public Short getAvgRunningCadence() {
      return getFieldShortValue(18, 0, Profile.SubFields.SESSION_MESG_AVG_CADENCE_FIELD_AVG_RUNNING_CADENCE);
   }

   /**
    * Set avg_running_cadence field
    * Units: strides/min
    *
    * @param avgRunningCadence
    */
   public void setAvgRunningCadence(Short avgRunningCadence) {
      setFieldValue(18, 0, avgRunningCadence, Profile.SubFields.SESSION_MESG_AVG_CADENCE_FIELD_AVG_RUNNING_CADENCE);
   }

   /**
    * Get max_cadence field
    * Units: rpm
    *
    * @return max_cadence
    */
   public Short getMaxCadence() {
      return getFieldShortValue(19, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set max_cadence field
    * Units: rpm
    *
    * @param maxCadence
    */
   public void setMaxCadence(Short maxCadence) {
      setFieldValue(19, 0, maxCadence, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get max_running_cadence field
    * Units: strides/min
    *
    * @return max_running_cadence
    */
   public Short getMaxRunningCadence() {
      return getFieldShortValue(19, 0, Profile.SubFields.SESSION_MESG_MAX_CADENCE_FIELD_MAX_RUNNING_CADENCE);
   }

   /**
    * Set max_running_cadence field
    * Units: strides/min
    *
    * @param maxRunningCadence
    */
   public void setMaxRunningCadence(Short maxRunningCadence) {
      setFieldValue(19, 0, maxRunningCadence, Profile.SubFields.SESSION_MESG_MAX_CADENCE_FIELD_MAX_RUNNING_CADENCE);
   }

   /**
    * Get avg_power field
    * Units: watts
    * Comment: total_power / total_timer_time if non_zero_avg_power otherwise total_power / total_elapsed_time
    *
    * @return avg_power
    */
   public Integer getAvgPower() {
      return getFieldIntegerValue(20, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set avg_power field
    * Units: watts
    * Comment: total_power / total_timer_time if non_zero_avg_power otherwise total_power / total_elapsed_time
    *
    * @param avgPower
    */
   public void setAvgPower(Integer avgPower) {
      setFieldValue(20, 0, avgPower, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get max_power field
    * Units: watts
    *
    * @return max_power
    */
   public Integer getMaxPower() {
      return getFieldIntegerValue(21, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set max_power field
    * Units: watts
    *
    * @param maxPower
    */
   public void setMaxPower(Integer maxPower) {
      setFieldValue(21, 0, maxPower, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get total_ascent field
    * Units: m
    *
    * @return total_ascent
    */
   public Integer getTotalAscent() {
      return getFieldIntegerValue(22, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set total_ascent field
    * Units: m
    *
    * @param totalAscent
    */
   public void setTotalAscent(Integer totalAscent) {
      setFieldValue(22, 0, totalAscent, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get total_descent field
    * Units: m
    *
    * @return total_descent
    */
   public Integer getTotalDescent() {
      return getFieldIntegerValue(23, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set total_descent field
    * Units: m
    *
    * @param totalDescent
    */
   public void setTotalDescent(Integer totalDescent) {
      setFieldValue(23, 0, totalDescent, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get total_training_effect field
    *
    * @return total_training_effect
    */
   public Float getTotalTrainingEffect() {
      return getFieldFloatValue(24, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set total_training_effect field
    *
    * @param totalTrainingEffect
    */
   public void setTotalTrainingEffect(Float totalTrainingEffect) {
      setFieldValue(24, 0, totalTrainingEffect, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get first_lap_index field
    *
    * @return first_lap_index
    */
   public Integer getFirstLapIndex() {
      return getFieldIntegerValue(25, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set first_lap_index field
    *
    * @param firstLapIndex
    */
   public void setFirstLapIndex(Integer firstLapIndex) {
      setFieldValue(25, 0, firstLapIndex, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get num_laps field
    *
    * @return num_laps
    */
   public Integer getNumLaps() {
      return getFieldIntegerValue(26, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set num_laps field
    *
    * @param numLaps
    */
   public void setNumLaps(Integer numLaps) {
      setFieldValue(26, 0, numLaps, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get event_group field
    *
    * @return event_group
    */
   public Short getEventGroup() {
      return getFieldShortValue(27, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set event_group field
    *
    * @param eventGroup
    */
   public void setEventGroup(Short eventGroup) {
      setFieldValue(27, 0, eventGroup, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get trigger field
    *
    * @return trigger
    */
   public SessionTrigger getTrigger() {
      Short value = getFieldShortValue(28, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return SessionTrigger.getByValue(value);
   }

   /**
    * Set trigger field
    *
    * @param trigger
    */
   public void setTrigger(SessionTrigger trigger) {
      setFieldValue(28, 0, trigger.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get nec_lat field
    * Units: semicircles
    *
    * @return nec_lat
    */
   public Integer getNecLat() {
      return getFieldIntegerValue(29, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set nec_lat field
    * Units: semicircles
    *
    * @param necLat
    */
   public void setNecLat(Integer necLat) {
      setFieldValue(29, 0, necLat, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get nec_long field
    * Units: semicircles
    *
    * @return nec_long
    */
   public Integer getNecLong() {
      return getFieldIntegerValue(30, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set nec_long field
    * Units: semicircles
    *
    * @param necLong
    */
   public void setNecLong(Integer necLong) {
      setFieldValue(30, 0, necLong, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get swc_lat field
    * Units: semicircles
    *
    * @return swc_lat
    */
   public Integer getSwcLat() {
      return getFieldIntegerValue(31, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set swc_lat field
    * Units: semicircles
    *
    * @param swcLat
    */
   public void setSwcLat(Integer swcLat) {
      setFieldValue(31, 0, swcLat, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get swc_long field
    * Units: semicircles
    *
    * @return swc_long
    */
   public Integer getSwcLong() {
      return getFieldIntegerValue(32, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set swc_long field
    * Units: semicircles
    *
    * @param swcLong
    */
   public void setSwcLong(Integer swcLong) {
      setFieldValue(32, 0, swcLong, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get normalized_power field
    * Units: watts
    *
    * @return normalized_power
    */
   public Integer getNormalizedPower() {
      return getFieldIntegerValue(34, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set normalized_power field
    * Units: watts
    *
    * @param normalizedPower
    */
   public void setNormalizedPower(Integer normalizedPower) {
      setFieldValue(34, 0, normalizedPower, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get training_stress_score field
    * Units: tss
    *
    * @return training_stress_score
    */
   public Float getTrainingStressScore() {
      return getFieldFloatValue(35, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set training_stress_score field
    * Units: tss
    *
    * @param trainingStressScore
    */
   public void setTrainingStressScore(Float trainingStressScore) {
      setFieldValue(35, 0, trainingStressScore, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get intensity_factor field
    * Units: if
    *
    * @return intensity_factor
    */
   public Float getIntensityFactor() {
      return getFieldFloatValue(36, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set intensity_factor field
    * Units: if
    *
    * @param intensityFactor
    */
   public void setIntensityFactor(Float intensityFactor) {
      setFieldValue(36, 0, intensityFactor, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get left_right_balance field
    *
    * @return left_right_balance
    */
   public Integer getLeftRightBalance() {
      return getFieldIntegerValue(37, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set left_right_balance field
    *
    * @param leftRightBalance
    */
   public void setLeftRightBalance(Integer leftRightBalance) {
      setFieldValue(37, 0, leftRightBalance, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get checksum field
    *
    * @return checksum
    */
   public Short getChecksum() {
      return getFieldShortValue(252, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set checksum field
    *
    * @param checksum
    */
   public void setChecksum(Short checksum) {
      setFieldValue(252, 0, checksum, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

}
