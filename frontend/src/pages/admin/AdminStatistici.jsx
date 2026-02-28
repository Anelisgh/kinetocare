import React, { useState, useEffect, useMemo } from 'react';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
  LineChart, Line, AreaChart, Area, PieChart, Pie, Cell
} from 'recharts';
import { adminService } from '../../services/adminService';
import '../../styles/adminShared.css';
import '../../styles/adminStatistici.css';

export default function AdminStatistici() {
  const [dateRange, setDateRange] = useState({
    startDate: new Date(new Date().getFullYear(), 0, 1).toISOString().split('T')[0], // 1 Jan current year
    endDate: new Date().toISOString().split('T')[0] // Today
  });

  const [filters, setFilters] = useState({
      locatieId: 'all',
      terapeutId: 'all'
  });

  const [referenceData, setReferenceData] = useState({
      locatii: [],
      terapeuti: [] // For filter dropdown
  });

  const [stats, setStats] = useState({
    programariLunare: [],
    venituriLocatie: [],
    rataAnulare: [],
    pacientiNoi: [],
    programariTerapeut: [],
    terapeutiActivi: []
  });

  const [processedData, setProcessedData] = useState({
      programariLunare: [],
      venituriLocatie: [],
      rataAnulare: [],
      pacientiNoi: [],
      programariTerapeut: [],
      terapeutiActivi: []
  });

  const [loading, setLoading] = useState(true);

  // 1. Fetch reference data (locations, therapists) on mount
  useEffect(() => {
      const fetchRefs = async () => {
          try {
              const [locatii, users] = await Promise.all([
                  adminService.getAllLocatii(),
                  adminService.getAllUsers('TERAPEUT', true) // Only active therapists
              ]);
              const formattedLocatii = (locatii || []).map(loc => ({
                  ...loc,
                  nume: loc.active === false ? `${loc.nume} (Inactiv)` : loc.nume
              }));
              setReferenceData({
                  locatii: formattedLocatii,
                  terapeuti: users || []
              });
          } catch (error) {
              console.error("Error fetching reference data:", error);
          }
      };
      fetchRefs();
  }, []);

  // 2. Fetch statistics when date range changes
  useEffect(() => {
    fetchData();
  }, [dateRange]);

  // 3. Process/Filter data when stats or filters change
  useEffect(() => {
      processAllData();
  }, [stats, filters, referenceData]);


  const fetchData = async () => {
    setLoading(true);
    try {
      const { startDate, endDate } = dateRange;
      const [
        programariLunare,
        venituriLocatie,
        rataAnulare,
        pacientiNoi,
        programariTerapeut,
        terapeutiActivi
      ] = await Promise.all([
        adminService.getProgramariLunare(startDate, endDate),
        adminService.getVenituriLocatie(startDate, endDate),
        adminService.getRataAnulare(startDate, endDate),
        adminService.getPacientiNoi(startDate, endDate),
        adminService.getProgramariTerapeut(startDate, endDate),
        adminService.getTerapeutiActivi()
      ]);

      setStats({
        programariLunare: programariLunare || [],
        venituriLocatie: venituriLocatie || [],
        rataAnulare: rataAnulare || [],
        pacientiNoi: pacientiNoi || [],
        programariTerapeut: programariTerapeut || [],
        terapeutiActivi: terapeutiActivi || []
      });

    } catch (error) {
      console.error('Error fetching statistics:', error);
    } finally {
      setLoading(false);
    }
  };

  const processAllData = () => {
      setProcessedData({
          programariLunare: processProgramariLunare(stats.programariLunare),
          venituriLocatie: filterByLocation(stats.venituriLocatie),
          rataAnulare: filterByLocation(stats.rataAnulare),
          pacientiNoi: processPacientiNoi(stats.pacientiNoi),
          programariTerapeut: processProgramariTerapeut(stats.programariTerapeut),
          terapeutiActivi: filterByLocation(stats.terapeutiActivi)
      });
  };

  // --- Processors ---

  const getFormattedLocatieNume = (locatieId, defaultNume) => {
      const loc = referenceData.locatii.find(l => l.id === locatieId);
      return loc ? loc.nume : defaultNume;
  };

  // Pivot: { month: "Jan 2024", "Loc A": 10, "Loc B": 5 }
  const processProgramariLunare = (data) => {
    if (!data) return [];
    
    // 1. Filter raw data first
    let filtered = data;
    if (filters.locatieId !== 'all') {
        filtered = data.filter(d => d.locatieId === Number(filters.locatieId));
    }

    // 2. Group by Month-Year
    const grouped = {};
    filtered.forEach(item => {
        const key = `${item.an}-${item.luna}`; // 2024-1
        if (!grouped[key]) {
            grouped[key] = {
                an: item.an,
                luna: item.luna,
                numeLuna: new Date(item.an, item.luna - 1).toLocaleString('ro-RO', { month: 'short', year: 'numeric' }),
                total: 0
            };
        }
        // If viewing ALL, separate by location name.
        const numeFormatat = getFormattedLocatieNume(item.locatieId, item.locatieNume);
        grouped[key][numeFormatat] = item.count;
        grouped[key].total += item.count;
    });

    // 3. Convert to array and sort
    return Object.values(grouped).sort((a, b) => {
        if (a.an !== b.an) return a.an - b.an;
        return a.luna - b.luna;
    });
  };

  const processPacientiNoi = (data) => {
    if (!data) return [];
    let filtered = data;
    if (filters.locatieId !== 'all') {
        filtered = data.filter(d => d.locatieId === Number(filters.locatieId));
    }
    
    // Aggregate by month (sum active new patients across selected locations)
    const grouped = {};
    filtered.forEach(item => {
        const key = `${item.an}-${item.luna}`;
        if (!grouped[key]) {
            grouped[key] = {
                numeLuna: new Date(item.an, item.luna - 1).toLocaleString('ro-RO', { month: 'short', year: 'numeric' }),
                nrPacientiNoi: 0,
                an: item.an,
                luna: item.luna
            };
        }
        grouped[key].nrPacientiNoi += item.numarPacientiNoi;
    });

    return Object.values(grouped).sort((a, b) => {
        if (a.an !== b.an) return a.an - b.an;
        return a.luna - b.luna;
    });
  }

  const processProgramariTerapeut = (data) => {
      if(!data) return [];
      let filtered = data;
      // Filter by therapist if selected
      if (filters.terapeutId !== 'all') {
          filtered = data.filter(d => d.terapeutId === Number(filters.terapeutId));
      }
      return [...filtered].sort((a,b) => b.count - a.count);
  }

  const filterByLocation = (data) => {
      if (!data) return [];
      let filtered = data;
      if (filters.locatieId !== 'all') {
         filtered = data.filter(d => d.locatieId === Number(filters.locatieId));
      }
      return filtered.map(item => ({
          ...item,
          locatieNume: getFormattedLocatieNume(item.locatieId, item.locatieNume)
      }));
  }


  const handleDateChange = (e) => {
    const { name, value } = e.target;
    setDateRange(prev => ({ ...prev, [name]: value }));
  };

  const handleFilterChange = (e) => {
      const { name, value } = e.target;
      setFilters(prev => ({ ...prev, [name]: value }));
  };

  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8', '#82ca9d', '#ffc658'];

  // 4. Compute Summary KPIs using useMemo
  const summaryKPIs = useMemo(() => {
      let totalVenituri = 0;
      let totalProgramari = 0;
      let totalPacientiNoi = 0;
      let rataAnulare = 0;

      // Venituri
      const venituriData = filters.locatieId === 'all' 
          ? stats.venituriLocatie 
          : stats.venituriLocatie.filter(d => d.locatieId === Number(filters.locatieId));
      totalVenituri = venituriData.reduce((acc, curr) => acc + curr.totalVenituri, 0);

      // Programari (from programariLunare)
      const progData = filters.locatieId === 'all'
          ? stats.programariLunare
          : stats.programariLunare.filter(d => d.locatieId === Number(filters.locatieId));
      totalProgramari = progData.reduce((acc, curr) => acc + curr.count, 0);

      // Pacienti Noi
      const pacientiData = filters.locatieId === 'all'
          ? stats.pacientiNoi
          : stats.pacientiNoi.filter(d => d.locatieId === Number(filters.locatieId));
      totalPacientiNoi = pacientiData.reduce((acc, curr) => acc + curr.numarPacientiNoi, 0);

      // Rata Anulare (from processedData to get the exact location array)
      if (processedData.rataAnulare.length > 0) {
          const sum = processedData.rataAnulare.reduce((acc, curr) => acc + curr.rataAnulare, 0);
          rataAnulare = (sum / processedData.rataAnulare.length).toFixed(2);
      }

      return { totalVenituri, totalProgramari, totalPacientiNoi, rataAnulare };
  }, [stats, processedData.rataAnulare, filters.locatieId]);


  if (loading && !stats.programariLunare.length) {
    return (
      <div className="admin-container">
         <div className="loading">Se încarcă statisticile...</div>
      </div>
    );
  }

  return (
    <div className="admin-container">
      <div className="admin-header">
        <div>
          <h1>Statistici Clinică</h1>
          <p>Vizualizare date și performanță - {dateRange.startDate} până la {dateRange.endDate}</p>
        </div>
      </div>

      <div className="filter-bar">
        {/* Date Filters */}
        <div className="filter-group">
          <label>Data Început</label>
          <input
            type="date"
            name="startDate"
            value={dateRange.startDate}
            onChange={handleDateChange}
            className="search-input"
          />
        </div>
        <div className="filter-group">
          <label>Data Sfârșit</label>
          <input
            type="date"
            name="endDate"
            value={dateRange.endDate}
            onChange={handleDateChange}
            className="search-input"
          />
        </div>

        {/* Location Filter */}
        <div className="filter-group">
            <label>Locație</label>
            <select
                name="locatieId"
                value={filters.locatieId}
                onChange={handleFilterChange}
                className="filter-select"
            >
                <option value="all">Toate Locațiile</option>
                {referenceData.locatii.map(loc => (
                    <option key={loc.id} value={loc.id}>{loc.nume}</option>
                ))}
            </select>
        </div>

        {/* Therapist Filter */}
        <div className="filter-group">
            <label>Terapeut (pt. grafic individual)</label>
            <select
                name="terapeutId"
                value={filters.terapeutId}
                onChange={handleFilterChange}
                className="filter-select"
            >
                <option value="all">Toți Terapeuții</option>
                {referenceData.terapeuti.map(terapeut => (
                    <option key={terapeut.id} value={terapeut.id}>
                        {terapeut.prenume} {terapeut.nume}
                    </option>
                ))}
            </select>
        </div>

        <button 
          className="btn-save-header" 
          onClick={fetchData} 
          disabled={loading}
        >
          {loading ? 'Se încarcă...' : 'Actualizează Datele'}
        </button>
      </div>

      {/* KPI Cards Section */}
      <div className="kpi-grid">
          <div className="kpi-card">
              <h4>Total Venituri</h4>
              <p className="kpi-value">{summaryKPIs.totalVenituri.toLocaleString('ro-RO')} RON</p>
          </div>
          <div className="kpi-card">
              <h4>Total Programări</h4>
              <p className="kpi-value">{summaryKPIs.totalProgramari}</p>
          </div>
          <div className="kpi-card">
              <h4>Total Pacienți Noi</h4>
              <p className="kpi-value">{summaryKPIs.totalPacientiNoi}</p>
          </div>
          <div className="kpi-card">
              <h4>Rata Medie Anulare</h4>
              <p className="kpi-value">{summaryKPIs.rataAnulare}%</p>
          </div>
      </div>

      <div className="charts-grid">
        
        {/* Row 1: Programari Lunare (Stacked/Grouped) */}
        <div className="chart-card full-width">
          <h3>Evoluție Programări Lunare {filters.locatieId !== 'all' ? `(${referenceData.locatii.find(l => l.id == filters.locatieId)?.nume})` : ''}</h3>
          <ResponsiveContainer width="100%" height={350}>
            <BarChart data={processedData.programariLunare} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="numeLuna" />
              <YAxis />
              <Tooltip />
              <Legend />
              {filters.locatieId === 'all' ? (
                  // If All locations, verify uniqueness or map all locations as bars
                  referenceData.locatii.map((loc, index) => (
                      <Bar key={loc.id} dataKey={loc.nume} stackId="a" fill={COLORS[index % COLORS.length]} radius={[4, 4, 0, 0]} />
                  ))
              ) : (
                  // If filtered, just show total or specific
                  <Bar dataKey="total" fill="#0ea5e9" name="Programări" radius={[4, 4, 0, 0]} />
              )}
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Row 2: Venituri & Rata Anulare */}
        <div className="chart-card">
          <h3>Venituri {filters.locatieId === 'all' ? 'per Locație' : 'Totale'}</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={processedData.venituriLocatie} layout={filters.locatieId !== 'all' ? 'horizontal' : 'vertical'} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
              <CartesianGrid strokeDasharray="3 3" />
              {filters.locatieId !== 'all' ? <XAxis type="category" dataKey="locatieNume" /> : <XAxis type="number" />}
              {filters.locatieId !== 'all' ? <YAxis type="number" /> : <YAxis type="category" dataKey="locatieNume" width={100} />}
              <Tooltip formatter={(value) => `${value} RON`} />
              <Legend />
              <Bar dataKey="totalVenituri" fill="#10b981" name="Venit Total" radius={[0, 4, 4, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-card">
            <h3>Rata de Anulare (%)</h3>
            <ResponsiveContainer width="100%" height={300}>
                <BarChart data={processedData.rataAnulare}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="locatieNume" />
                    <YAxis />
                    <Tooltip formatter={(value) => `${value}%`} />
                    <Legend />
                    <Bar dataKey="rataAnulare" fill="#f43f5e" name="Rata Anulare" radius={[4, 4, 0, 0]} />
                </BarChart>
            </ResponsiveContainer>
        </div>

        {/* Row 3: Pacienti Noi & Terapeuti Activi */}
        <div className="chart-card full-width">
            <h3>Pacienți Noi (Evoluție)</h3>
            <ResponsiveContainer width="100%" height={300}>
                <AreaChart data={processedData.pacientiNoi}>
                    <defs>
                        <linearGradient id="colorPacienti" x1="0" y1="0" x2="0" y2="1">
                            <stop offset="5%" stopColor="#8b5cf6" stopOpacity={0.8}/>
                            <stop offset="95%" stopColor="#8b5cf6" stopOpacity={0}/>
                        </linearGradient>
                    </defs>
                    <CartesianGrid strokeDasharray="3 3" vertical={false} />
                    <XAxis dataKey="numeLuna" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    <Area type="monotone" dataKey="nrPacientiNoi" stroke="#8b5cf6" fillOpacity={1} fill="url(#colorPacienti)" name="Pacienți Noi" strokeWidth={3} />
                </AreaChart>
            </ResponsiveContainer>
        </div>

        <div className="chart-card full-width">
            <h3>Terapeuți Activi per Locație</h3>
             <ResponsiveContainer width="100%" height={300}>
                <BarChart data={processedData.terapeutiActivi}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="locatieNume" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    <Bar dataKey="numarTerapeutiActivi" fill="#ec4899" name="Terapeuți Activi" radius={[4, 4, 0, 0]} />
                </BarChart>
            </ResponsiveContainer>
        </div>
        
        <div className="chart-card full-width">
             <h3>{filters.terapeutId === 'all' ? 'Top Programări per Terapeut' : 'Programări Terapeut Selectat'}</h3>
             <ResponsiveContainer width="100%" height={400}>
                 <BarChart data={processedData.programariTerapeut} layout="vertical" margin={{ left: 50 }}>
                     <CartesianGrid strokeDasharray="3 3" />
                     <XAxis type="number" />
                     <YAxis type="category" dataKey="terapeutNume" width={150} />
                     <Tooltip />
                     <Legend />
                     <Bar dataKey="count" fill="#6366f1" name="Nr. Programări" radius={[0, 4, 4, 0]} />
                 </BarChart>
             </ResponsiveContainer>
        </div>

      </div>
    </div>
  );
}
