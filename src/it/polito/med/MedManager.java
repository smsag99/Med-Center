package it.polito.med;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MedManager {
	TreeSet<String> specialitiesCollection = new TreeSet<>();
	// docID doc
	TreeMap<String, Doctor> doctorsCollection = new TreeMap<>();
	// apID , ap
	TreeMap<String, Appointment> appointmentsCollection = new TreeMap<>();
	String currentDay;

	/**
	 * add a set of medical specialities to the list of specialities
	 * offered by the med centre.
	 * Method can be invoked multiple times.
	 * Possible duplicates are ignored.
	 * 
	 * @param specialities the specialities
	 */
	public void addSpecialities(String... specialities) {
		for (int i = 0; i < specialities.length; i++) {
			specialitiesCollection.add(specialities[i]);
		}
	}

	/**
	 * retrieves the list of specialities offered in the med centre
	 * 
	 * @return list of specialities
	 */
	public Collection<String> getSpecialities() {
		return specialitiesCollection;
	}

	/**
	 * adds a new doctor with the list of their specialities
	 * 
	 * @param id         unique id of doctor
	 * @param name       name of doctor
	 * @param surname    surname of doctor
	 * @param speciality speciality of the doctor
	 * @throws MedException in case of duplicate id or non-existing speciality
	 */
	public void addDoctor(String id, String name, String surname, String speciality) throws MedException {
		if (!specialitiesCollection.contains(speciality))
			throw new MedException();
		Doctor doctor = new Doctor(id, name, surname, speciality);
		doctorsCollection.put(id, doctor);
	}

	/**
	 * retrieves the list of doctors with the given speciality
	 * 
	 * @param speciality required speciality
	 * @return the list of doctor ids
	 */
	public Collection<String> getSpecialists(String speciality) {
		return doctorsCollection.values().stream().filter(x -> x.is(speciality)).map(Doctor::getName).toList();
	}

	/**
	 * retrieves the name of the doctor with the given code
	 * 
	 * @param code code id of the doctor
	 * @return the name
	 */
	public String getDocName(String code) {
		return doctorsCollection.get(code).getName();
	}

	/**
	 * retrieves the surname of the doctor with the given code
	 * 
	 * @param code code id of the doctor
	 * @return the surname
	 */
	public String getDocSurname(String code) {
		return doctorsCollection.get(code).getSurname();
	}

	/**
	 * Define a schedule for a doctor on a given day.
	 * Slots are created between start and end hours with a
	 * duration expressed in minutes.
	 * 
	 * @param code     doctor id code
	 * @param date     date of schedule
	 * @param start    start time
	 * @param end      end time
	 * @param duration duration in minutes
	 * @return the number of slots defined
	 */
	public int addDailySchedule(String code, String date, String start, String end, int duration) throws MedException {
		if (!doctorsCollection.containsKey(code))
			throw new MedException("invalid Doctor ID");
		int startInMin = (Integer.parseInt(start.split(":")[0]) * 60) + Integer.parseInt(start.split(":")[1]);
		int endInMin = (Integer.parseInt(end.split(":")[0]) * 60) + Integer.parseInt(end.split(":")[1]);

		if (startInMin >= endInMin)
			throw new MedException("invalid Time");
		int numSlots = doctorsCollection.get(code).addSchedual(date, startInMin, endInMin, duration);
		return numSlots;
	}

	/**
	 * retrieves the available slots available on a given date for a speciality.
	 * The returned map contains an entry for each doctor that has slots scheduled
	 * on the date.
	 * The map contains a list of slots described as strings with the format
	 * "hh:mm-hh:mm",
	 * e.g. "14:00-14:30" describes a slot starting at 14:00 and lasting 30 minutes.
	 * 
	 * @param date       date to look for
	 * @param speciality required speciality
	 * @return a map doc-id -> list of slots in the schedule
	 */
	public Map<String, List<String>> findSlots(String date, String speciality) {
		TreeMap<String, List<String>> docSlots = new TreeMap<>();
		List<Doctor> docs = doctorsCollection.values().stream().filter(x -> x.getSpeciality().equals(speciality))
				.filter(x -> x.hasSchedual(date)).collect(Collectors.toList());
		for (Doctor d : docs) {
			docSlots.put(d.getId(), d.getScheduals(date));
		}
		return docSlots;
	}

	/**
	 * Define an appointment for a patient in an existing slot of a doctor's
	 * schedule
	 * 
	 * @param ssn     ssn of the patient
	 * @param name    name of the patient
	 * @param surname surname of the patient
	 * @param code    code id of the doctor
	 * @param date    date of the appointment
	 * @param slot    slot to be booked
	 * @return a unique id for the appointment
	 * @throws MedException in case of invalid code, date or slot
	 */
	public String setAppointment(String ssn, String name, String surname, String code, String date, String slot)
			throws MedException {
		if (!doctorsCollection.containsKey(code))
			throw new MedException("no doctor with this id");
		if (!doctorsCollection.get(code).hasSchedual(date))
			throw new MedException("there is no schedual on this date");
		if (!doctorsCollection.get(code).getScheduals(date).contains(slot))
			throw new MedException("there is no slots for given time");
		Appointment appointment = new Appointment(ssn, name, surname, code, date, slot);
		appointmentsCollection.put(appointment.getId(), appointment);
		return appointment.getId();
	}

	/**
	 * retrieves the doctor for an appointment
	 * 
	 * @param idAppointment id of appointment
	 * @return doctor code id
	 */
	public String getAppointmentDoctor(String idAppointment) {
		return appointmentsCollection.get(idAppointment).getCode();

	}

	/**
	 * retrieves the patient for an appointment
	 * 
	 * @param idAppointment id of appointment
	 * @return doctor patient ssn
	 */
	public String getAppointmentPatient(String idAppointment) {
		return appointmentsCollection.get(idAppointment).getSSN();
	}

	/**
	 * retrieves the time for an appointment
	 * 
	 * @param idAppointment id of appointment
	 * @return time of appointment
	 */
	public String getAppointmentTime(String idAppointment) {
		return appointmentsCollection.get(idAppointment).getSlot().split("-")[0];
	}

	/**
	 * retrieves the date for an appointment
	 * 
	 * @param idAppointment id of appointment
	 * @return date
	 */
	public String getAppointmentDate(String idAppointment) {
		return appointmentsCollection.get(idAppointment).getDate();
	}

	/**
	 * retrieves the list of a doctor appointments for a given day.
	 * Appointments are reported as string with the format
	 * "hh:mm=SSN"
	 * 
	 * @param code doctor id
	 * @param date date required
	 * @return list of appointments
	 */
	public Collection<String> listAppointments(String code, String date) {
		return appointmentsCollection.values().stream().filter(x -> x.getCode().equals(code))
				.filter(x -> x.getDate().equals(date)).map(Appointment::toString).collect(Collectors.toList());
	}

	/**
	 * Define the current date for the medical centre
	 * The date will be used to accept patients arriving at the centre.
	 * 
	 * @param date current date
	 * @return the number of total appointments for the day
	 */
	public int setCurrentDate(String date) {
		this.currentDay = date;
		return (int) appointmentsCollection.values().stream().filter(x -> x.getDate().equals(date)).count();

	}

	/**
	 * mark the patient as accepted by the med centre reception
	 * 
	 * @param ssn SSN of the patient
	 */
	public void accept(String ssn) {
		appointmentsCollection.values().stream().filter(x -> x.getSSN().equals(ssn))
				.filter(x -> x.getDate().equals(currentDay)).findFirst().get().accept();

	}

	/**
	 * returns the next appointment of a patient that has been accepted.
	 * Returns the id of the earliest appointment whose patient has been
	 * accepted and the appointment not completed yet.
	 * Returns null if no such appointment is available.
	 * 
	 * @param code code id of the doctor
	 * @return appointment id
	 */
	public String nextAppointment(String code) {
		Optional<Appointment> app = appointmentsCollection.values().stream()
				.filter(x -> x.getCode().equals(code) && x.isAccepted() && !x.isCompleted())
				.findFirst();
		if (app.isPresent())
			return app.get().getId();
		return null;
	}

	/**
	 * mark an appointment as complete.
	 * The appointment must be with the doctor with the given code
	 * the patient must have been accepted
	 * 
	 * @param code  doctor code id
	 * @param appId appointment id
	 * @throws MedException in case code or appointment code not valid,
	 *                      or appointment with another doctor
	 *                      or patient not accepted
	 *                      or appointment not for the current day
	 */
	public void completeAppointment(String code, String appId) throws MedException {
		if (!doctorsCollection.containsKey(code))
			throw new MedException("doctor not found");
		if (!appointmentsCollection.containsKey(appId))
			throw new MedException("appointment nit found");
		Appointment app = appointmentsCollection.get(appId);
		if (app == null)
			throw new MedException("appointment not found");
		if (!app.getCode().equals(code))
			throw new MedException("this appointment doesn't belong to this doctor");
		app.complete();
	}

	/**
	 * computes the show rate for the appointments of a doctor on a given date.
	 * The rate is the ratio of accepted patients over the number of appointments
	 * 
	 * @param code doctor id
	 * @param date reference date
	 * @return no show rate
	 */
	public double showRate(String code, String date) {
		int totoalAppOfDoc = (int) appointmentsCollection.values().stream()
				.filter(x -> x.getDate().equals(date) && x.getCode().equals(code))
				.count();
		int acceptedAppOfDoc = (int) appointmentsCollection.values().stream()
				.filter(x -> x.getDate().equals(date) && x.getCode().equals(code) && x.isAccepted())
				.count();

		return (double) acceptedAppOfDoc / totoalAppOfDoc;
	}

	/**
	 * computes the schedule completeness for all doctors of the med centre.
	 * The completeness for a doctor is the ratio of the number of appointments
	 * over the number of slots in the schedule.
	 * The result is a map that associates to each doctor id the relative
	 * completeness
	 * 
	 * @return the map id : completeness
	 */
	public Map<String, Double> scheduleCompleteness() {
		TreeMap<String, Double> complentness = new TreeMap<>();
		for (Doctor doc : doctorsCollection.values()) {
			int numAppointment = (int) appointmentsCollection.values().stream()
					.filter(x -> x.getCode().equals(doc.getId())).count();
			int numSlots = doc.getAllScheduals();
			double ratio = (double) numAppointment / numSlots;
			complentness.put(doc.getId(), ratio);
		}
		return complentness;
	}

}
