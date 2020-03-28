package io.pivotal.pal.tracker;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class InMemoryTimeEntryRepository implements TimeEntryRepository {
    private HashMap<Long, TimeEntry> timeEntries = new HashMap<>();
    // ids will start at 1 not 0
    private long currentId = 1L;

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        Long id = currentId++;
        TimeEntry result = new TimeEntry(id,
                timeEntry.getProjectId(),
                timeEntry.getUserId(),
                timeEntry.getDate(),
                timeEntry.getHours());

        timeEntries.put(id, result);
        return result;
    }

    @Override
    public TimeEntry find(Long id) {
        return timeEntries.get(id);
    }

    @Override
    public List<TimeEntry> list() {
        return new ArrayList<>(timeEntries.values());
    }

    @Override
    public TimeEntry update(Long id, TimeEntry timeEntry) {
        if (find(id) == null) {
            return null;
        }

        TimeEntry result = new TimeEntry(id, timeEntry.getProjectId(),
                timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());

        timeEntries.replace(id, result);
        return result;
    }

    @Override
    public void delete(Long id) {
        timeEntries.remove(id);
    }
}
