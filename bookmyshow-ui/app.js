const API_BASE = "http://localhost:8080/api/v1";
const FALLBACK_MOVIES = [
    { id: 1, title: "Dune: Part Two", language: "English", genre: "Sci-Fi / Adventure", certificate: "UA", posterUrl: "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=900" },
    { id: 2, title: "Monkey Man", language: "English", genre: "Action / Thriller", certificate: "A", posterUrl: "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=900" },
    { id: 3, title: "Kung Fu Panda 4", language: "English", genre: "Animation / Comedy", certificate: "U", posterUrl: "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?w=900" },
    { id: 4, title: "Jawan", language: "Hindi", genre: "Action / Thriller", certificate: "UA", posterUrl: "https://images.unsplash.com/photo-1574267432553-4b4628081c31?w=900" },
    { id: 5, title: "Stree 2: Sarkate Ka Aatank", language: "Hindi", genre: "Horror / Comedy", certificate: "UA", posterUrl: "https://images.unsplash.com/photo-1509248961158-e54f6934749c?w=900" }
];

const savedProfile = JSON.parse(localStorage.getItem("showtimeProfile") || "null");
const state = { movies: [], shows: [], selectedShow: null, selectedSeats: [], selectedDate: dateKey(new Date(Date.now() + 86400000)), activeFilter: "All", profile: savedProfile, pendingShowId: null };
const $ = (selector) => document.querySelector(selector);

function dateKey(date) { return date.toISOString().slice(0, 10); }
function formatTime(value) { return new Date(value).toLocaleTimeString([], { hour: "numeric", minute: "2-digit" }); }
function money(value) { return `₹${Number(value).toLocaleString("en-IN")}`; }
function escapeHtml(value = "") { return String(value).replace(/[&<>'"]/g, character => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", "'": "&#39;", '"': "&quot;" }[character])); }
function normalizePhone(value) { return String(value || "").replace(/\D/g, ""); }

async function fetchJson(path, options = {}) {
    const response = await fetch(`${API_BASE}${path}`, { headers: { "Content-Type": "application/json" }, ...options });
    if (!response.ok) {
        const error = await response.json().catch(() => ({}));
        throw new Error(error.message || "Something went wrong");
    }
    return response.json();
}

function renderMovies() {
    const query = $("#searchInput").value.toLowerCase().trim();
    const movies = state.movies.filter(movie => {
        const matchesQuery = `${movie.title} ${movie.genre} ${movie.language}`.toLowerCase().includes(query);
        const matchesFilter = state.activeFilter === "All" || movie.language === state.activeFilter || movie.genre.includes(state.activeFilter);
        return matchesQuery && matchesFilter;
    });
    $("#movieGrid").innerHTML = movies.length ? movies.map((movie, index) => `
        <article class="movie-card" style="animation-delay:${index * 90}ms">
            <div class="poster-wrap">
                <img src="${escapeHtml(movie.posterUrl)}" alt="${escapeHtml(movie.title)} poster" loading="lazy">
                <span class="poster-badge">${escapeHtml(movie.certificate || "NOW SHOWING")}</span>
            </div>
            <div class="movie-info">
                <div><h3>${escapeHtml(movie.title)}</h3><p>${escapeHtml(movie.language)} · ${escapeHtml(movie.genre)}</p></div>
                <div class="movie-rating"><span>★</span> 8.${index + 1}</div>
            </div>
            <button class="movie-action" data-movie="${escapeHtml(movie.title)}" type="button">Find showtimes <span>↗</span></button>
        </article>`).join("") : `<div class="empty-state">No movies match that search yet.</div>`;
    document.querySelectorAll(".movie-action").forEach(button => button.addEventListener("click", () => {
        $("#showtimesSection").scrollIntoView({ behavior: "smooth" });
        const target = state.shows.find(show => show.movie.title === button.dataset.movie);
        if (target) openSeatModal(target.id);
        else showToast("Choose a showtime below to book this movie.", false);
    }));
}

function renderHero() {
    const movie = state.movies[0];
    if (!movie) return;
    $("#heroImage").src = movie.posterUrl;
    $("#heroImage").alt = `${movie.title} artwork`;
    $("#heroTitle").innerHTML = `${escapeHtml(movie.title)}<br><em>on the biggest screen.</em>`;
    $("#heroDescription").textContent = movie.description || "Big screens, first-day energy, and a seat with your name on it.";
    $("#heroLanguage").textContent = movie.language;
    $("#heroGenre").textContent = movie.genre;
    $("#heroCount").textContent = String(state.movies.length).padStart(2, "0");
}

function renderDates() {
    const dates = Array.from({ length: 5 }, (_, index) => new Date(Date.now() + (index + 1) * 86400000));
    $("#datePicker").innerHTML = dates.map(date => {
        const key = dateKey(date);
        const day = indexDay(date);
        return `<button class="date-button ${key === state.selectedDate ? "active" : ""}" data-date="${key}" type="button"><span>${day}</span><strong>${date.getDate()}</strong></button>`;
    }).join("");
    document.querySelectorAll(".date-button").forEach(button => button.addEventListener("click", () => {
        state.selectedDate = button.dataset.date;
        renderDates();
        loadShows();
    }));
}
function indexDay(date) { return date.toLocaleDateString([], { weekday: "short" }); }

function renderShows() {
    const grouped = state.shows.reduce((groups, show) => {
        const key = show.movie.id;
        if (!groups[key]) groups[key] = { movie: show.movie, theatre: show.theatre, shows: [] };
        groups[key].shows.push(show);
        return groups;
    }, {});
    const entries = Object.values(grouped);
    $("#showList").innerHTML = entries.length ? entries.map(group => `
        <article class="show-row">
            <div class="show-movie"><img class="mini-poster" src="${escapeHtml(group.movie.posterUrl)}" alt=""><div><h3>${escapeHtml(group.movie.title)}</h3><p>${escapeHtml(group.movie.language)} · ${escapeHtml(group.movie.durationMinutes)} min</p></div></div>
            <div class="show-theatre"><strong>${escapeHtml(group.theatre.name)}</strong><p class="theatre-address">${escapeHtml(group.theatre.address)}</p></div>
            <div class="show-times">${group.shows.map(show => `<button class="time-button" data-show-id="${show.id}" type="button">${formatTime(show.startsAt)} · ${money(show.ticketPrice)}</button>`).join("")}</div>
        </article>`).join("") : `<div class="empty-state">No shows found for this date. Try another day.</div>`;
    document.querySelectorAll(".time-button").forEach(button => button.addEventListener("click", () => openSeatModal(Number(button.dataset.showId))));
}

async function loadMovies() {
    try { state.movies = await fetchJson("/movies"); } catch (error) { state.movies = FALLBACK_MOVIES; showToast("Showing sample movies. Start the backend to book tickets.", false); }
    renderHero();
    renderMovies();
}
async function loadShows() {
    const city = encodeURIComponent($("#citySelect").value);
    $("#showList").innerHTML = `<div class="loading-state"><span class="loader"></span>Finding shows in ${escapeHtml($("#citySelect").value)}...</div>`;
    try { state.shows = await fetchJson(`/shows?city=${city}&date=${state.selectedDate}`); renderShows(); }
    catch (error) { state.shows = []; $("#showList").innerHTML = `<div class="empty-state">Connect the Spring Boot backend to see live showtimes.</div>`; }
}

function openSeatModal(showId) {
    if (!state.profile) {
        state.pendingShowId = showId;
        openAccountModal();
        showToast("Create a profile or log in before choosing seats.", false);
        return;
    }
    state.selectedShow = state.shows.find(show => show.id === showId);
    state.selectedSeats = [];
    $("#modalTitle").textContent = "Select your seats";
    $("#modalSubtitle").textContent = `${state.selectedShow.movie.title} · ${state.selectedShow.theatre.name} · ${formatTime(state.selectedShow.startsAt)}`;
    renderSeats();
    $("#bookingModal").hidden = false;
    document.body.style.overflow = "hidden";
}
function renderSeats() {
    const seatLabels = Array.from({ length: state.selectedShow.totalSeats }, (_, index) => index < 10 ? `A${index + 1}` : `B${index - 9}`);
    const availableLabels = new Set(state.selectedShow.availableSeatLabels || seatLabels);
    $("#seatGrid").innerHTML = seatLabels.map(label => {
        const occupied = !availableLabels.has(label);
        const selected = state.selectedSeats.includes(label);
        return `<button class="seat ${occupied ? "occupied" : ""} ${selected ? "selected" : ""}" ${occupied ? "disabled" : ""} data-seat="${label}" type="button">${label}</button>`;
    }).join("");
    document.querySelectorAll(".seat:not(.occupied)").forEach(button => button.addEventListener("click", () => {
        const label = button.dataset.seat;
        state.selectedSeats = state.selectedSeats.includes(label) ? state.selectedSeats.filter(seat => seat !== label) : [...state.selectedSeats, label];
        renderSeats();
    }));
    const total = state.selectedSeats.length * Number(state.selectedShow.ticketPrice);
    $("#totalAmount").textContent = money(total);
    $("#continueButton").disabled = !state.selectedSeats.length;
}
function closeModals() {
    $("#bookingModal").hidden = true;
    $("#customerModal").hidden = true;
    $("#accountModal").hidden = true;
    document.body.style.overflow = "";
}
function showToast(message, success = true) { const toast = $("#successToast"); toast.textContent = message; toast.style.borderLeftColor = success ? "#48bd8c" : "#f5c84b"; toast.classList.add("visible"); setTimeout(() => toast.classList.remove("visible"), 5000); }

function openAccountModal() {
    $("#accountModal").hidden = false;
    document.body.style.overflow = "hidden";
    renderProfile();
}

function renderProfile() {
    const profile = state.profile;
    $("#profileAuth").hidden = Boolean(profile);
    $("#profileView").hidden = !profile;
    if (!profile) return;
    $("#profileDisplayName").textContent = profile.name;
    $("#profileDisplayContact").textContent = `${profile.email} · ${profile.phone}`;
    loadBookingHistory();
}

function setProfile(profile) {
    state.profile = profile;
    localStorage.setItem("showtimeProfile", JSON.stringify(profile));
    renderProfile();
    if (state.pendingShowId) {
        const showId = state.pendingShowId;
        state.pendingShowId = null;
        closeModals();
        openSeatModal(showId);
    }
}

function renderBookingHistory(bookings) {
    $("#bookingHistory").innerHTML = bookings.length ? bookings.map(booking => `
        <article class="booking-card ${booking.status === "CANCELLED" ? "cancelled" : ""}">
            <div class="booking-card-heading"><strong>${escapeHtml(booking.movieTitle)}</strong><span class="booking-status">${escapeHtml(booking.status)}</span></div>
            <p>${escapeHtml(booking.theatreName)} · ${formatTime(booking.bookedAt)}</p>
            <div class="booking-card-meta"><span>Seats <strong>${escapeHtml(booking.seatLabels.join(", "))}</strong></span><strong>${money(booking.totalAmount)}</strong></div>
            ${booking.status === "CONFIRMED" ? `<button class="cancel-booking" data-booking-id="${booking.id}" type="button">Cancel booking</button>` : ""}
        </article>`).join("") : `<div class="empty-state booking-empty">No bookings found for this number.</div>`;
    document.querySelectorAll(".cancel-booking").forEach(button => button.addEventListener("click", () => cancelBooking(Number(button.dataset.bookingId))));
}

async function loadBookingHistory() {
    if (!state.profile) return;
    $("#bookingHistory").innerHTML = `<div class="loading-state"><span class="loader"></span>Finding your bookings...</div>`;
    try {
        const bookings = await fetchJson(`/profiles/${state.profile.id}/bookings`);
        renderBookingHistory(bookings);
    } catch (error) {
        $("#bookingHistory").innerHTML = `<p class="form-error">${escapeHtml(error.message || "Unable to load bookings")}</p>`;
    }
}

async function cancelBooking(bookingId) {
    if (!state.profile || !window.confirm("Cancel this booking and release the seats?")) return;
    try {
        await fetchJson(`/bookings/${bookingId}/cancel?profileId=${state.profile.id}`, { method: "POST" });
        showToast("Booking cancelled and seats released.");
        await loadBookingHistory();
        loadShows();
    } catch (error) {
        showToast(error.message || "Unable to cancel booking", false);
    }
}

$("#searchInput").addEventListener("input", renderMovies);
document.querySelectorAll(".filter-button").forEach(button => button.addEventListener("click", () => {
    state.activeFilter = button.dataset.filter;
    document.querySelectorAll(".filter-button").forEach(item => item.classList.toggle("active", item === button));
    renderMovies();
}));
$("#citySelect").addEventListener("change", loadShows);
$("#heroBrowseButton").addEventListener("click", () => $("#moviesSection").scrollIntoView({ behavior: "smooth" }));
$("#viewAllButton").addEventListener("click", () => {
    $("#searchInput").value = "";
    state.activeFilter = "All";
    document.querySelectorAll(".filter-button").forEach(item => item.classList.toggle("active", item.dataset.filter === "All"));
    renderMovies();
    $("#moviesSection").scrollIntoView({ behavior: "smooth" });
});
$("#closeModalButton").addEventListener("click", closeModals);
$("#closeCustomerButton").addEventListener("click", closeModals);
$("#closeAccountButton").addEventListener("click", closeModals);
$("#profileButton").addEventListener("click", openAccountModal);
$("#createTab").addEventListener("click", () => {
    $("#createTab").classList.add("active");
    $("#loginTab").classList.remove("active");
    $("#profileCreateForm").hidden = false;
    $("#profileLoginForm").hidden = true;
});
$("#loginTab").addEventListener("click", () => {
    $("#loginTab").classList.add("active");
    $("#createTab").classList.remove("active");
    $("#profileCreateForm").hidden = true;
    $("#profileLoginForm").hidden = false;
});
$("#profileCreateForm").addEventListener("submit", async event => {
    event.preventDefault();
    const error = $("#profileCreateError");
    error.textContent = "";
    try {
        const profile = await fetchJson("/profiles", { method: "POST", body: JSON.stringify({ name: $("#profileName").value, email: $("#profileEmail").value, phone: $("#profilePhone").value }) });
        setProfile(profile);
        showToast(`Welcome, ${profile.name}. Your profile is ready.`);
    } catch (profileError) { error.textContent = profileError.message; }
});
$("#profileLoginForm").addEventListener("submit", async event => {
    event.preventDefault();
    const error = $("#profileLoginError");
    error.textContent = "";
    try {
        const profile = await fetchJson(`/profiles/login?identifier=${encodeURIComponent($("#profileIdentifier").value)}`);
        setProfile(profile);
        showToast(`Welcome back, ${profile.name}.`);
    } catch (profileError) { error.textContent = profileError.message; }
});
$("#logoutButton").addEventListener("click", () => {
    state.profile = null;
    localStorage.removeItem("showtimeProfile");
    renderProfile();
    showToast("You have been logged out.");
});
$("#continueButton").addEventListener("click", () => {
    $("#bookingModal").hidden = true;
    $("#formSeats").textContent = `${state.selectedSeats.join(", ")} · ${state.selectedSeats.length} seat${state.selectedSeats.length > 1 ? "s" : ""}`;
    $("#formTotal").textContent = money(state.selectedSeats.length * Number(state.selectedShow.ticketPrice));
    $("#bookingProfileLabel").textContent = `Booking as ${state.profile.name} · ${state.profile.email}`;
    $("#customerModal").hidden = false;
});
$("#customerForm").addEventListener("submit", async event => {
    event.preventDefault();
    const error = $("#bookingError");
    error.textContent = "";
    const submit = event.target.querySelector("button[type=submit]");
    submit.disabled = true;
    try {
        const booking = await fetchJson(`/bookings/shows/${state.selectedShow.id}`, { method: "POST", body: JSON.stringify({ profileId: state.profile.id, seatLabels: state.selectedSeats }) });
        closeModals();
        event.target.reset();
        showToast(`Booking #${booking.id} confirmed. Your seats are ${booking.seatLabels.join(", ")}.`);
        loadShows();
    } catch (bookingError) { error.textContent = bookingError.message; }
    finally { submit.disabled = false; }
});

document.addEventListener("keydown", event => { if (event.key === "Escape") closeModals(); });
renderDates();
loadMovies();
loadShows();
