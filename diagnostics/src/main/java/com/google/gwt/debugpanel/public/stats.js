var stats = window.__stats = [];
window.__gwtStatsEvent = function(evt) {
    stats[stats.length] = evt;
    var listener = window.__stats_listener;
    listener && listener(evt);
    return true;
}