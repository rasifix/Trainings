function(data) {
  // $.log(data)
  var p;
  return {
    activities : data.rows.map(function(r) {
      p = (r.value) || {};
      p.date = r.key;
      p.speed = r.value.speed * 3.6;
      if (r.value.hr)
        p.hr = r.value.hr.avg;
      p.altGain = r.value.alt.gain;
      p.altLoss = r.value.alt.loss;
      return p;
    })
  }
};