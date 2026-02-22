export class AppError extends Error {
  constructor(message, status = 500, eroriCampuri = null, detail = null) {
    super(message);
    this.name = 'AppError';
    this.status = status;
    this.eroriCampuri = eroriCampuri; // ex: { email: "Email invalid", parola: "Prea scurta" }
    this.detail = detail; // detaliile standardizate RFC 7807
  }
}
