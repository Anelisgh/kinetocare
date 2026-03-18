import React from 'react';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer
} from 'recharts';

const JurnalEvolutieChart = ({ data }) => {
  if (!data || data.length === 0) {
    return null;
  }

  return (
    <div className="jurnal-trend-container">
      <h3 className="trend-title">Evoluție Jurnal (Ultimele 10 ședințe)</h3>
      <div className="trend-chart-wrapper">
        <ResponsiveContainer width="100%" height={300}>
          <LineChart
            data={data}
            margin={{
              top: 5,
              right: 30,
              left: 0,
              bottom: 5,
            }}
          >
            <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f0f0f0" />
            <XAxis 
              dataKey="data" 
              axisLine={false}
              tickLine={false}
              tick={{ fill: '#666', fontSize: 12 }}
              dy={10}
            />
            <YAxis 
              domain={[0, 10]} 
              axisLine={false}
              tickLine={false}
              tick={{ fill: '#666', fontSize: 12 }}
              dx={-5}
            />
            <Tooltip 
              contentStyle={{ 
                borderRadius: '8px', 
                border: 'none', 
                boxShadow: '0 4px 12px rgba(0,0,0,0.1)' 
              }} 
            />
            <Legend 
              verticalAlign="top" 
              height={36} 
              iconType="circle"
            />
            <Line
              name="Durere"
              type="monotone"
              dataKey="nivelDurere"
              stroke="#ff4d4f"
              strokeWidth={3}
              dot={{ r: 4, strokeWidth: 2 }}
              activeDot={{ r: 6 }}
              animationDuration={1500}
            />
            <Line
              name="Dificultate"
              type="monotone"
              dataKey="dificultateExercitii"
              stroke="#1890ff"
              strokeWidth={3}
              dot={{ r: 4, strokeWidth: 2 }}
              activeDot={{ r: 6 }}
              animationDuration={1500}
            />
            <Line
              name="Oboseală"
              type="monotone"
              dataKey="nivelOboseala"
              stroke="#faad14"
              strokeWidth={3}
              dot={{ r: 4, strokeWidth: 2 }}
              activeDot={{ r: 6 }}
              animationDuration={1500}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>

      <style jsx>{`
        .jurnal-trend-container {
          background: white;
          border-radius: 12px;
          padding: 24px;
          margin-bottom: 24px;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        }
        .trend-title {
          font-size: 1.1rem;
          color: #333;
          margin-bottom: 20px;
          font-weight: 600;
        }
        .trend-chart-wrapper {
          width: 100%;
        }
      `}</style>
    </div>
  );
};

export default JurnalEvolutieChart;
