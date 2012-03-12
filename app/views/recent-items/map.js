function(doc) {
  if (doc.activity) {
    emit(doc.activity.date, doc.activity);
  }
};