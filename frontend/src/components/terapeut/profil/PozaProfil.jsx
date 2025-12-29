import { useState, useEffect } from 'react';
import '../../../styles/pozaProfil.css';

const PozaProfil = ({ pozaProfil, isEditing, onChange }) => {
  const [preview, setPreview] = useState(pozaProfil);

  useEffect(() => {
    if (pozaProfil) {
      setPreview(pozaProfil);
    }
  }, [pozaProfil]);
  // Handle image file selection and convert to base64
  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        const base64String = reader.result;
        setPreview(base64String);
        onChange({
          target: { name: 'pozaProfil', value: base64String },
        });
      };
      reader.readAsDataURL(file);
    }
  };

  return (
    <div className="poza-profil-container">
      <img
        src={preview || 'https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png?20150327203541'}
        alt="Poza profil"
        className="poza-profil-img"
      />
      {isEditing && (
        <div className="form-group">
          {/* <label htmlFor="pozaProfil">Schimbă poza</label> */}
          <input
            type="file"
            id="pozaProfil"
            name="pozaProfil"
            accept="image/png, image/jpeg"
            onChange={handleImageChange}
          />
        </div>
      )}
    </div>
  );
};

export default PozaProfil;
