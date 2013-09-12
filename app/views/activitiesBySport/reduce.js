function(key,values,rereduce) {
  // {
  //   sport: "CYCLING", 
  //   totalTime: 2461, 
  //   distance: 14223, 
  //   speed: 5.78, 
  //   hr: {avg: 130, max: 168}, 
  //   alt: {min: 481, avg: 551, max: 592, gain: 401, loss: 388, start: 559, end: 563}
  // }
  var result = { };
  result.count = 0;
  result.totalTime = 0;
  result.distance = 0;
  result.alt = { };
  result.alt.min = 10000000;
  result.alt.max = -10000000;
  result.alt.gain = 0;
  result.alt.loss = 0;

  var hr = {
    avg : 0,
    max : -1
  };
  var hrtime = 0;

  var cadence = {
    avg : 0,
    max : -1	
  }
  var cadencetime = 0;

  for (idx in values) {
	  var value = values[idx];
	  result.totalTime += value.totalTime;
	  result.distance += value.distance;
	  if (value.alt) {
	    result.alt.min = Math.min(result.alt.min, value.alt.min);
	    result.alt.max = Math.max(result.alt.max, value.alt.max);
	    result.alt.gain += value.alt.gain;
	    result.alt.loss += value.alt.loss;
    }
	  if (value.hr) {
	    hr.avg += value.hr.avg * value.totalTime;
	    hr.max = Math.max(hr.max, value.hr.max);
	    hrtime += value.totalTime;
	  }
	  if (value.cadence) {
	    cadence.avg += value.cadence.avg * value.totalTime;
	    cadence.max = Math.max(cadence.max, value.cadence.max);
	    cadencetime += value.totalTime;
	  }
	  if (value.count !== undefined) {
	    result.count += value.count;
	  } else {
	    result.count += 1;
	  }
  }

  if (result.totalTime != 0) {
    result.speed = result.distance / result.totalTime;
  }

  if (hrtime != 0) {
	result.hr = hr;
    result.hr.avg = Math.round(result.hr.avg / hrtime);	
  }

  if (cadencetime != 0) {
    result.cadence = cadence;
    result.cadence.avg = Math.round(result.cadence.avg / cadencetime);	
  }
  
  return result;
}
